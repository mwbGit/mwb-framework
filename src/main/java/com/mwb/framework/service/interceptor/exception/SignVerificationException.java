package com.mwb.framework.service.interceptor.exception;

public class SignVerificationException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public SignVerificationException() {
		super();
	}

	public SignVerificationException(String message, Throwable cause) {
		super(message, cause);
	}

	public SignVerificationException(String message) {
		super(message);
	}

	public SignVerificationException(Throwable cause) {
		super(cause);
	}

	
}
