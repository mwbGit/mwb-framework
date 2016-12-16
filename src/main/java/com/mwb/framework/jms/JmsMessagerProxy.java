package com.mwb.framework.jms;


import com.mwb.framework.api.common.jms.Path;
import com.mwb.framework.api.common.jms.Queue;
import com.mwb.framework.api.common.jms.Topic;
import com.mwb.framework.util.ReflectionUtility;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@SuppressWarnings("rawtypes")
public final class JmsMessagerProxy implements FactoryBean, InitializingBean {

	private JmsTemplate jmsTemplate;
	
	private ActiveMQConnectionFactory connectionFactory;
	
	private Class proxiedClass;
	
	private String destinationName;
	
	@Override
	public Object getObject() throws Exception {
		
		return Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(), 
				new Class[] {proxiedClass}, 
				new InvocationHandler() {
					
					@Override
					public Object invoke(Object proxy, Method method,
							final Object[] args) throws Throwable {
						
						Annotation annotation = method.getAnnotation(Path.class);
						
						if (annotation != null) {
							final Path path = (Path) annotation;
							
							jmsTemplate.send(destinationName, new MessageCreator() {
								@Override
								public Message createMessage(Session session) throws JMSException {
									
									ObjectMessage message = session.createObjectMessage(args);

									message.setStringProperty("path", path.value());

									return message;
								}
							});
						}
						
						return null;
					}
				});
	}

	@Override
	public Class getObjectType() {
		return proxiedClass;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setProxiedClass(String proxiedClass) throws ClassNotFoundException {
		this.proxiedClass = Class.forName(proxiedClass);
	}

	@Autowired
	public void setConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Annotation queue = ReflectionUtility.getAnnotationFromInterface(this.proxiedClass, Queue.class);
		Annotation topic = ReflectionUtility.getAnnotationFromInterface(this.proxiedClass, Topic.class);
		
		if (queue != null && topic != null) {
			throw new RuntimeException("Queue and topic annotation cannot be used together!");
		} else if (queue != null) {
			destinationName = "/queue/" + ((Queue)queue).address();
			
			this.jmsTemplate = new JmsTemplate(connectionFactory);
			this.jmsTemplate.setPubSubDomain(false);
		} else if (topic != null) {
			destinationName = "VirtualTopic." + ((Topic)topic).address();
			
			this.jmsTemplate = new JmsTemplate(connectionFactory);
			this.jmsTemplate.setPubSubDomain(true);
		} else {
			throw new RuntimeException("Not a queue or topic messager class!");
		}
	}
	
}
