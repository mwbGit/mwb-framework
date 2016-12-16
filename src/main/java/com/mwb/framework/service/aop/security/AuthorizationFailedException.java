package com.mwb.framework.service.aop.security;

public class AuthorizationFailedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AuthorizationFailedException() {
		super();
	}

	public AuthorizationFailedException(String message) {
		super(message);
	}

	public AuthorizationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthorizationFailedException(Throwable cause) {
		super(cause);
	}

}
