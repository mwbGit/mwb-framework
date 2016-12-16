package com.mwb.framework.service.aop.normalize;

public class RequestNormalizeException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public RequestNormalizeException() {
		super();
	}

	public RequestNormalizeException(String message, Throwable cause) {
		super(message, cause);
	}

	public RequestNormalizeException(String message) {
		super(message);
	}

	public RequestNormalizeException(Throwable cause) {
		super(cause);
	}
}
