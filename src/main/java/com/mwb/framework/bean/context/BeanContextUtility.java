package com.mwb.framework.bean.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class BeanContextUtility implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;
	
	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}
	
	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}
	
	public static <T> Map<String, T> getBeansOfType(Class<T> clazz) throws BeansException {
		return applicationContext.getBeansOfType(clazz);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		BeanContextUtility.applicationContext = applicationContext;
	}
   
}
