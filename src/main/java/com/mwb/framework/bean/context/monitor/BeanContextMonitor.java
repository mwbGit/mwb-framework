package com.mwb.framework.bean.context.monitor;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class BeanContextMonitor implements ApplicationListener<ContextRefreshedEvent> {

	private static boolean contextRefreshed;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() != null) {
			return;
		}
		setContextRefreshed(true);		
	}

	public static boolean isContextRefreshed() {
		return contextRefreshed;
	}

	public static void setContextRefreshed(boolean contextRefreshed) {
		BeanContextMonitor.contextRefreshed = contextRefreshed;
	}
}
