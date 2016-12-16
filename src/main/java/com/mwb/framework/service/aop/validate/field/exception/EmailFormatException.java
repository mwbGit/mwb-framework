package com.mwb.framework.service.aop.validate.field.exception;

public class EmailFormatException extends ValidationException {
	
	private static final long serialVersionUID = 1L;

	public EmailFormatException() {
		super();
	}

	public EmailFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmailFormatException(String message) {
		super(message);
	}

	public EmailFormatException(Throwable cause) {
		super(cause);
	}
}
