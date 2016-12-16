package com.mwb.framework.security.cas;

import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class DefaultCasAuthenticationFilter extends CasAuthenticationFilter {
	
	public void setServiceProperties(CasServiceProperties serviceProperties) {
		setFilterProcessesUrl(serviceProperties.getFilterProcessesUrl());
	}

	@Override
	public void setAuthenticationSuccessHandler(
			AuthenticationSuccessHandler successHandler) {
		if (successHandler == null) {
			return;
		}
		
		super.setAuthenticationSuccessHandler(successHandler);
	}
	
}
