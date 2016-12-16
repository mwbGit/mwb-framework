package com.mwb.framework.service.interceptor.jsonp;

import org.apache.cxf.jaxrs.provider.jsonp.JsonpJaxrsWriterInterceptor;

public class DefaultJsonpJaxrsWriterInterceptor extends JsonpJaxrsWriterInterceptor {

	public DefaultJsonpJaxrsWriterInterceptor() {
		setMediaType("text/javascript");
		setPaddingEnd("(function(){return");
	}
}
