package com.mwb.framework.service.aop.security;


public class PermissionDeniedException extends AuthorizationFailedException {
	private static final long serialVersionUID = 1L;

	public PermissionDeniedException() {
		super();
	}

	public PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PermissionDeniedException(String message) {
		super(message);
	}

	public PermissionDeniedException(Throwable cause) {
		super(cause);
	}

}
