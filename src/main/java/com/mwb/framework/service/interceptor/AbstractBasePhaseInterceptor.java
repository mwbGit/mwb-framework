package com.mwb.framework.service.interceptor;

import com.mwb.framework.service.StatusCodeManager;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;

import javax.ws.rs.core.Response;

public abstract class AbstractBasePhaseInterceptor extends AbstractPhaseInterceptor<Message> {

	public AbstractBasePhaseInterceptor(String phase) {
		super(phase);
	}

	protected void handleException(Message message, Exception e) {
		message.getExchange().put(Response.class, 
				StatusCodeManager.toResponse(e));
	}
}
