package com.mwb.framework.hazelcast;

public class LockInterruptedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LockInterruptedException() {
		super();
	}

	public LockInterruptedException(String message, Throwable cause) {
		super(message, cause);
	}

	public LockInterruptedException(String message) {
		super(message);
	}

	public LockInterruptedException(Throwable cause) {
		super(cause);
	}
	
}
