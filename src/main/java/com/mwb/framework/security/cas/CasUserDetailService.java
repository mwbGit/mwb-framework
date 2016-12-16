package com.mwb.framework.security.cas;

import com.mwb.framework.security.IUserDetailsBuilder;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class CasUserDetailService implements
		AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
	
	private IUserDetailsBuilder userDetailsBuilder;
	
	@SuppressWarnings("unchecked")
	@Override
	public UserDetails loadUserDetails(CasAssertionAuthenticationToken token)
			throws UsernameNotFoundException {
	
		Assertion assertion = token.getAssertion();
		AttributePrincipal principal = assertion.getPrincipal();
		
		return (userDetailsBuilder != null) ? 
				userDetailsBuilder.build(principal.getAttributes()) : null;
	}

	public void setUserDetailsBuilder(IUserDetailsBuilder userDetailsBuilder) {
		this.userDetailsBuilder = userDetailsBuilder;
	}
}
