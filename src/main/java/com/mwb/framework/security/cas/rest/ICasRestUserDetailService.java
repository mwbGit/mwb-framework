package com.mwb.framework.security.cas.rest;

import org.springframework.security.core.userdetails.UserDetails;

public interface ICasRestUserDetailService {
	
	public String verifyToken(String token);
	
	public UserDetails loadUserDetails(String token, String ticket);
	
}
