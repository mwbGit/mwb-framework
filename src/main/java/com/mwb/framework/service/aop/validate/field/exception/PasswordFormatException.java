package com.mwb.framework.service.aop.validate.field.exception;

public class PasswordFormatException extends ValidationException {
	
	private static final long serialVersionUID = 1L;

	public PasswordFormatException() {
		super();
	}

	public PasswordFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public PasswordFormatException(String message) {
		super(message);
	}

	public PasswordFormatException(Throwable cause) {
		super(cause);
	}
}
