package com.mwb.framework.service.aop.validate.field.exception;

public class NumericalRangeFormatException extends ValidationException {
	
	private static final long serialVersionUID = 1L;

	public NumericalRangeFormatException() {
		super();
	}

	public NumericalRangeFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public NumericalRangeFormatException(String message) {
		super(message);
	}

	public NumericalRangeFormatException(Throwable cause) {
		super(cause);
	}
}
