package com.mwb.framework.service.aop.security;


public class NoLoginSessionException extends AuthorizationFailedException {

	private static final long serialVersionUID = 1440028273305989697L;

	public NoLoginSessionException() {
		super();
	}

	public NoLoginSessionException(String message) {
		super(message);
	}

	public NoLoginSessionException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoLoginSessionException(Throwable cause) {
		super(cause);
	}

}
