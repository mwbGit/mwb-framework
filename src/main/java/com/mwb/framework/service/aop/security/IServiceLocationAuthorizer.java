package com.mwb.framework.service.aop.security;

import com.mwb.framework.model.Location;

import java.util.List;

public interface IServiceLocationAuthorizer {
	
	public boolean authorize(List<Location> locations);
	
}
