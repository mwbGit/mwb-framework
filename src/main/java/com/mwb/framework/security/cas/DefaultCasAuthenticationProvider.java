package com.mwb.framework.security.cas;

import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;

public class DefaultCasAuthenticationProvider extends CasAuthenticationProvider {

	public DefaultCasAuthenticationProvider(CasUserDetailService userDetailService) {
		setKey("security");
		setAuthenticationUserDetailsService(userDetailService);
	}

	@Override
	public void setServiceProperties(ServiceProperties serviceProperties) {
		super.setServiceProperties(serviceProperties);
		
		String serverUrl = ((CasServiceProperties)serviceProperties).getCasServerUrl();
		
		setTicketValidator(new Cas20ServiceTicketValidator(serverUrl));
	}
	
}
