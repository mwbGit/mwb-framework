package com.mwb.framework.hazelcast;

public class LockTimeoutException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public LockTimeoutException() {
		super();
	}

	public LockTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public LockTimeoutException(String message) {
		super(message);
	}

	public LockTimeoutException(Throwable cause) {
		super(cause);
	}

}
