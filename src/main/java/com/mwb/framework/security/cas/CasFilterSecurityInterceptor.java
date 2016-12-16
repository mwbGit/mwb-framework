package com.mwb.framework.security.cas;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

public class CasFilterSecurityInterceptor extends FilterSecurityInterceptor {
	
	public CasFilterSecurityInterceptor() {
		setAuthenticationManager(new AuthenticationManager() {

			@Override
			public Authentication authenticate(Authentication authentication)
					throws AuthenticationException {
				throw new AuthenticationException("Not implement yet!") {
					private static final long serialVersionUID = 1L;
				};
			}
			
		});
	}
}
