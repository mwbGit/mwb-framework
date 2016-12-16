package com.mwb.framework.service.interceptor.jsonp;

import org.apache.cxf.jaxrs.provider.jsonp.JsonpPreStreamInterceptor;

public class DefaultJsonpPreStreamInterceptor extends JsonpPreStreamInterceptor {
	
	public DefaultJsonpPreStreamInterceptor() {
		super();
		
		setMediaType("text/javascript");
		setPaddingEnd("(function(){return");
	}
}
