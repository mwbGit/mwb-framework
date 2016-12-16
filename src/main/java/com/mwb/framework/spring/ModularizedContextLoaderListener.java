package com.mwb.framework.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

public class ModularizedContextLoaderListener extends ContextLoaderListener {
	private static final String ENABLED_MODULES_PARAM = "enabledModules";

	@Override
	protected void configureAndRefreshWebApplicationContext(
			ConfigurableWebApplicationContext wac, ServletContext sc) {
		
		if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
			// The application context id is still set to its original default value
			// -> assign a more useful id based on available information
			String idParam = sc.getInitParameter(CONTEXT_ID_PARAM);
			if (idParam != null) {
				wac.setId(idParam);
			}
			else {
				// Generate default id...
				if (sc.getMajorVersion() == 2 && sc.getMinorVersion() < 5) {
					// Servlet <= 2.4: resort to name specified in web.xml, if any.
					wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
							ObjectUtils.getDisplayString(sc.getServletContextName()));
				}
				else {
					wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
							ObjectUtils.getDisplayString(sc.getContextPath()));
				}
			}
		}

		// Determine parent for root web application context, if any.
		ApplicationContext parent = loadParentContext(sc);

		wac.setParent(parent);
		wac.setServletContext(sc);
		
		List<String> configLocations = new ArrayList<String>();
		
		String configLocation = sc.getInitParameter(CONFIG_LOCATION_PARAM);
		if (configLocation != null) {
			configLocations.add(configLocation);	
		}
		
		String enabledModules = sc.getInitParameter(ENABLED_MODULES_PARAM);
		if (enabledModules != null) {
			String[] modules = enabledModules.split(",");
			if (modules != null && modules.length > 0) {
				for (String module : modules) {
					configLocations.add("classpath*:/spring/root/" + module.trim() + ".module.xml");
				}
			}
		}
		
		if (configLocations.size() > 0) {
			String[] locations = configLocations.toArray(new String[configLocations.size()]);
			
			wac.setConfigLocations(locations);
		}
		
		customizeContext(sc, wac);
		wac.refresh();
	}

	
}
