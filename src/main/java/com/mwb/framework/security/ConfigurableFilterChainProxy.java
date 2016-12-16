package com.mwb.framework.security;

import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

public class ConfigurableFilterChainProxy extends FilterChainProxy {
	
	private static final String LOCAL_LOGIN = "local";
	private static final String CAS_LOGIN = "cas";
	
	public ConfigurableFilterChainProxy(
			String filterChainType,
			List<SecurityFilterChain> formLoginFilterChains, 
			List<SecurityFilterChain> casLoginFilterChains) {
		super(determinFilterChains(filterChainType, formLoginFilterChains, casLoginFilterChains));
	}
	
	public static List<SecurityFilterChain> determinFilterChains(
			String filterChainType,
			List<SecurityFilterChain> formLoginFilterChains, 
			List<SecurityFilterChain> casLoginFilterChains) {
		
		if (LOCAL_LOGIN.equals(filterChainType)) {
			return formLoginFilterChains;
		} else if(CAS_LOGIN.equals(filterChainType)) {
			return casLoginFilterChains;
		}
		
		return null;
	}
}
