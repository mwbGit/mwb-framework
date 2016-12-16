package com.mwb.framework.service.aop.validate.field.exception;

import com.mwb.framework.service.api.exception.ParamedException;

public class IllegalRequestParameterValidateException extends ParamedException {
	
	private static final long serialVersionUID = 1L;

	public IllegalRequestParameterValidateException() {
		super();
	}

	public IllegalRequestParameterValidateException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalRequestParameterValidateException(String message, Object... params) {
		super(message);
		setParams(params);
	}

	public IllegalRequestParameterValidateException(Throwable cause) {
		super(cause);
	}
	   
}
