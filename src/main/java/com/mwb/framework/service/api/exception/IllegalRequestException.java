package com.mwb.framework.service.api.exception;

public class IllegalRequestException extends RuntimeException {

    private static final long serialVersionUID = -498442938337763721L;

    public IllegalRequestException() {
        super();
    }

    public IllegalRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalRequestException(String message) {
        super(message);
    }

    public IllegalRequestException(Throwable cause) {
        super(cause);
    }
}
