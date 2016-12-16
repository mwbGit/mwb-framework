package com.mwb.framework.service.interceptor.jsonp;

import org.apache.cxf.jaxrs.provider.jsonp.JsonpInInterceptor;

public class DefaultJsonpInInterceptor extends JsonpInInterceptor {

	public DefaultJsonpInInterceptor() {
		super();
		
		setCallbackParam("callback");
	}

}
