package com.mwb.framework.i18n;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class DefaultResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

	private static final String DEFAULT_RESOURCE = "classpath:/locale/default";
	
	@Override
	public void setBasenames(String[] basenames) {
		String[] newBasenames = new String[basenames.length + 1];
		
		newBasenames[0] = DEFAULT_RESOURCE;
		
		for (int i = 0; i < basenames.length; i++) {
			newBasenames[i + 1] = basenames[i];
		}
		
		super.setBasenames(newBasenames);
	}
	
	
}
