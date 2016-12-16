package com.mwb.framework.spring;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class ModularizedDispatcherServlet extends DispatcherServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected WebApplicationContext createWebApplicationContext(
			ApplicationContext parent) {
		Class<?> contextClass = getContextClass();
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Servlet with name '" + getServletName() +
					"' will try to create custom WebApplicationContext context of class '" +
					contextClass.getName() + "'" + ", using parent context [" + parent + "]");
		}
		if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
			throw new ApplicationContextException(
					"Fatal initialization error in servlet with name '" + getServletName() +
					"': custom WebApplicationContext class [" + contextClass.getName() +
					"] is not of type ConfigurableWebApplicationContext");
		}
		ConfigurableWebApplicationContext wac =
				(ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);

		wac.setParent(parent);
		
		String[] locations = new String[2];
		locations[0] = getContextConfigLocation();
		locations[1] = "classpath*:/spring/mvc/base.module.xml";
				
		wac.setConfigLocations(locations);

		configureAndRefreshWebApplicationContext(wac);

		return wac;
	}
	
}
