package com.mwb.framework.service.aop.validate.field.exception;

public class NotEmptyException extends ValidationException {
	
	private static final long serialVersionUID = 1L;

	public NotEmptyException() {
		super();
	}

	public NotEmptyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotEmptyException(String message) {
		super(message);
	}

	public NotEmptyException(Throwable cause) {
		super(cause);
	}
}
