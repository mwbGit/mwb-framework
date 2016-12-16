package com.mwb.framework.event;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.ConnectionFactory;

public class EventListenerContainer extends DefaultMessageListenerContainer implements ApplicationContextAware {
	
	private ApplicationContext applicationContext;
	
	@Override
	public void afterPropertiesSet() {
		setConnectionFactory((ConnectionFactory)applicationContext.getBean("eventConnectionFactory"));
		setMessageListener(applicationContext.getBean(EventManager.class));
		
		super.afterPropertiesSet();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}
