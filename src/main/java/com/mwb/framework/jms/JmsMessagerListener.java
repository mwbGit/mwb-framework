package com.mwb.framework.jms;

import com.mwb.framework.api.common.jms.*;
import com.mwb.framework.log.Log;
import com.mwb.framework.util.ReflectionUtility;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class JmsMessagerListener implements BeanPostProcessor, DisposableBean, ApplicationListener<ContextRefreshedEvent> {

	private static final Log LOG = Log.getLog(JmsMessagerListener.class);
	
	private ActiveMQConnectionFactory connectionFactory;

	private List<DefaultMessageListenerContainer> containers;
	
	public JmsMessagerListener() {	
		containers = new ArrayList<DefaultMessageListenerContainer>();
	}
	
	@Autowired
	public void setConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		
		if (!bean.getClass().getName().contains("Proxy")) {
			
			String concurrency = null;
			if (bean instanceof IConfigurable) {
				IConfigurable configurer = (IConfigurable)bean;
				
				concurrency = configurer.getConcurrency();
			}
			
			if (StringUtils.isBlank(concurrency)) {
				concurrency = AbstractConfigurable.CONCURRENCY;
			}
			
			Annotation queue = ReflectionUtility.getAnnotationFromInterfaceByRecursion(bean.getClass(), Queue.class);
			Annotation topic = ReflectionUtility.getAnnotationFromInterfaceByRecursion(bean.getClass(), Topic.class);
			
			if (queue != null && topic != null) {
				throw new RuntimeException("Queue and topic annotation cannot be used together!");
			} else if (queue != null || topic != null) {
				DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
				
				container.setConcurrency(concurrency);
				container.setConnectionFactory(connectionFactory);
				container.setMessageListener(new QueueMessageListener(bean));
				container.setPubSubDomain(false);
				
				String destationName = null;
				if (queue != null) {
					destationName = "/queue/" + ((Queue)queue).address();
				} else {
					destationName = "Consumer." + getTopicConsumerName(bean) + ".VirtualTopic." + ((Topic)topic).address();
				}
				
				ActiveMQQueue destination = new ActiveMQQueue(destationName);
				
				container.setDestination(destination);
				
				container.afterPropertiesSet();
				
				containers.add(container);
			}
		}
		
		return bean;
	}
	
	private static String getTopicConsumerName(Object bean) {
		
		if (bean instanceof IConfigurable) {
			IConfigurable configurer = (IConfigurable)bean;
			
			return configurer.getTopicConsumerName();
		}
		
		return StringUtils.replaceChars(bean.getClass().getName(), '.', '/');
	}
	
	private class QueueMessageListener implements MessageListener {
		
		private Object jmsMessager;
		
		private Map<String, Method> methodObjs;
		
		public QueueMessageListener(Object jmsMessager) {
			this.jmsMessager = jmsMessager;
			
			methodObjs = new HashMap<String, Method>();
			
			Method[] methods = jmsMessager.getClass().getMethods();
			for (Method method : methods) {
				if (Modifier.isPublic(method.getModifiers())
						&& !Modifier.isStatic(method.getModifiers())) {
					Annotation annotation = ReflectionUtility.getMethodAnnotationFromInterfaceByRecursion(method.getDeclaringClass(), method, Path.class);
					if (annotation != null) {
						Path path = (Path) annotation;
						
						methodObjs.put(path.value(), method);
					}
				}
			}
		}
		
		@Override
		public void onMessage(Message message) {
			try {
				if (message instanceof ObjectMessage) {
					ObjectMessage objectMessage = (ObjectMessage)message;

					Object[] args = (Object[])objectMessage.getObject();
					String path = (String)objectMessage.getStringProperty("path");
					
					Method methodObj = methodObjs.get(path);
					if (methodObj == null) {
						return;
					}
					
					methodObj.invoke(jmsMessager, args);
				}	
			} catch (Exception e) {
				LOG.error("Catch an exception!", e);
			}
		}

	}

	@Override
	public void destroy() throws Exception {
		if (containers != null) {
			for (DefaultMessageListenerContainer container : containers) {
				container.stop();
				container.destroy();
			}
		}
		
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() != null) {
			return;
		}
		
		if (containers != null) {
			for (DefaultMessageListenerContainer container : containers) {
				container.start();
			}
		}
		
	}

}
