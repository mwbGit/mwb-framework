package com.mwb.framework.service.provider;

import com.mwb.framework.service.StatusCodeManager;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;


public class ExceptionMapperProvider implements ExceptionMapper<Exception> {
	
	@Override
	public Response toResponse(Exception exception) {
		return StatusCodeManager.toResponse(exception);
	}

}
