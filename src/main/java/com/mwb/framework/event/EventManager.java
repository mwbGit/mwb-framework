package com.mwb.framework.event;

import com.mwb.framework.log.Log;
import org.apache.activemq.ScheduledMessage;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class EventManager implements MessageListener, BeanPostProcessor {
	private final static Log LOG = Log.getLog(EventManager.class);

	private final static String DEFAULT_QUEUE_NAME = "694829AD7CC00A5D9293A317F4E3C8F1";
	
	private final static Map<String, IEventHandler> EVENT_HANDLER_MAP;

	private final static Map<Class<? extends AbstractBaseEvent>, Set<IEventHandler>> EVENT_HANDLER_SET_MAP;

	private final static String EVENT_HANDLER_CLASS_NAME = "EVENT_HANDLER_CLASS_NAME";
	
	private static JmsTemplate jmsTemplate;
	
	static {
		EVENT_HANDLER_MAP = new HashMap<String, IEventHandler>();
		EVENT_HANDLER_SET_MAP = new HashMap<Class<? extends AbstractBaseEvent>, Set<IEventHandler>>();
	}

	public static void send(final AbstractBaseEvent event) {
		if (event instanceof AbstractDestinationEvent) {
			send(((AbstractDestinationEvent)event).getDestinationName(), event, null, null);
		} else {
			send(DEFAULT_QUEUE_NAME, event, null, null);
		}
	}
	
	public static void send(final String destinationName, final AbstractBaseEvent event) {
		send(destinationName, event, null, null);
	}

	public static void send(final String destinationName, final AbstractBaseEvent event, Class<? extends IEventHandler> handlerType) {
		send(destinationName, event, handlerType, null);
	}

	public static void send(final String destinationName, final AbstractBaseEvent event, final Class<? extends IEventHandler> handlerType, final Long millisecondsDelayed) {
		LOG.debug("Trying to send JMS event {}.", event);
		
		jmsTemplate.send(destinationName, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage(event);

				if (handlerType != null) {
					String className = handlerType.getName();

					IEventHandler registeredHandler = EVENT_HANDLER_MAP.get(className);

					if (registeredHandler == null) {
						String error = String.format("No instance for event handler %s", className);

						LOG.error(error);

						throw new RuntimeException(error);
					} 

					message.setStringProperty(EVENT_HANDLER_CLASS_NAME, className);
				}

				if (millisecondsDelayed != null && millisecondsDelayed > 0) {
					message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, millisecondsDelayed);
				}

				return message;
			}
		});

		LOG.debug("Succeeded to send JMS event {}.", event);
	}
	
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof ObjectMessage) {
				ObjectMessage objectMessage = (ObjectMessage)message;

				Object obj = objectMessage.getObject();
				if (obj != null) {
					if (obj instanceof AbstractBaseEvent) {
						AbstractBaseEvent event = (AbstractBaseEvent)obj;

						LOG.info("Trying to process JMS event {}.", event);

						String className = message.getStringProperty(EVENT_HANDLER_CLASS_NAME);
						if (StringUtils.isNotBlank(className)) {
							IEventHandler handler = EVENT_HANDLER_MAP.get(className);

							if (handler != null) {
								handler.process(event);
							} else {
								LOG.error("Cannot find event handler instance for {}", className);
							}
						} else {
							handleEvent(event);
						}
					} else {
						LOG.error("Received invalid event object {}", obj.getClass().getName());
					}
				} else {
					LOG.error("Received null event object");
				}

			} else {
				LOG.error("Received invalid type of message {}", message.getClass().getName());
			}
		} catch (JMSException e) {
			LOG.error("Catch an exception!", e);
		}
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {

		if (bean instanceof IEventHandler) {
			IEventHandler handler = (IEventHandler)bean;

			registerHandler(handler.getEventType(), handler);
		}

		return bean;
	}

	private void registerHandler(Class<? extends AbstractBaseEvent> clazz, IEventHandler handler) {
		// 注册IEventHandler
		String className = handler.getClass().getName();
		if (EVENT_HANDLER_MAP.keySet().contains(className)) {
			String error = String.format("Duplicate event handler %s instance!", className);

			throw new RuntimeException(error);
		}

		EVENT_HANDLER_MAP.put(handler.getClass().getName(), handler);

		// 注册Event和IEventHandler的映射关系
		Set<IEventHandler> handlers = EVENT_HANDLER_SET_MAP.get(clazz);

		if (handlers == null) {
			handlers = new HashSet<IEventHandler>();
		}

		if (handlers.contains(handler)) {
			String error = String.format("Duplicate event handler %s for %s!", handler.getClass().getName(), 
					clazz.getName());

			throw new RuntimeException(error);
		}

		handlers.add(handler);

		EVENT_HANDLER_SET_MAP.put(clazz, handlers);
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	private void handleEvent(AbstractBaseEvent event) {
		Class<? extends AbstractBaseEvent> clazz = event.getClass();

		Set<IEventHandler> handlers = EVENT_HANDLER_SET_MAP.get(clazz);
		if (handlers != null) {
			for (IEventHandler handler : handlers) {
				try {
					handler.process(event);
				} catch (Exception e) {
					LOG.error("Catch an exception!", e);
				}
			}
		} else {
			LOG.error("No handler is registered for this event {}", event.getClass().getName());
		}
	}

	public void setConnectionFactory(ConnectionFactory cf) {
		EventManager.jmsTemplate = new JmsTemplate(cf);
	}
}
