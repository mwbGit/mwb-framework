package com.mwb.framework.service.aop.validate.field.exception;

public class PagingRequestFormatException extends ValidationException {
	
	private static final long serialVersionUID = 1L;

	public PagingRequestFormatException() {
		super();
	}

	public PagingRequestFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public PagingRequestFormatException(String message) {
		super(message);
	}

	public PagingRequestFormatException(Throwable cause) {
		super(cause);
	}
}
