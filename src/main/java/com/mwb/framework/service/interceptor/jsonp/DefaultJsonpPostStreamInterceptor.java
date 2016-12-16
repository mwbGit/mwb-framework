package com.mwb.framework.service.interceptor.jsonp;

import org.apache.cxf.jaxrs.provider.jsonp.JsonpPostStreamInterceptor;

public class DefaultJsonpPostStreamInterceptor extends JsonpPostStreamInterceptor {
	
	public DefaultJsonpPostStreamInterceptor() {
		super();
		
		setPaddingEnd(";});");
	}
}
