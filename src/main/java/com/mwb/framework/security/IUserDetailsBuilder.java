package com.mwb.framework.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface IUserDetailsBuilder {
	public UserDetails build(Map<String, Object> attributes);
}
