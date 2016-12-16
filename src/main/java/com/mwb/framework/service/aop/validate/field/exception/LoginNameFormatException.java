package com.mwb.framework.service.aop.validate.field.exception;

public class LoginNameFormatException extends ValidationException {
	
	private static final long serialVersionUID = 1L;

	public LoginNameFormatException() {
		super();
	}

	public LoginNameFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoginNameFormatException(String message) {
		super(message);
	}

	public LoginNameFormatException(Throwable cause) {
		super(cause);
	}
}
