package com.mwb.framework.service.aop.validate.field.exception;

public class NotNullException extends ValidationException {
	
	private static final long serialVersionUID = 1L;

	public NotNullException() {
		super();
	}

	public NotNullException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotNullException(String message) {
		super(message);
	}

	public NotNullException(Throwable cause) {
		super(cause);
	}
}
