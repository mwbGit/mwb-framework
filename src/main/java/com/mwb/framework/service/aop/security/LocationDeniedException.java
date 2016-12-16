package com.mwb.framework.service.aop.security;


public class LocationDeniedException extends AuthorizationFailedException {
	private static final long serialVersionUID = 1L;

	public LocationDeniedException() {
		super();
	}

	public LocationDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	public LocationDeniedException(String message) {
		super(message);
	}

	public LocationDeniedException(Throwable cause) {
		super(cause);
	}

}
