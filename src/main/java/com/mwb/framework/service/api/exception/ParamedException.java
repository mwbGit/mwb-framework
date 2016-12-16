package com.mwb.framework.service.api.exception;

public class ParamedException extends RuntimeException {

	private static final long serialVersionUID = -4500926985173206156L;
	
	protected Object[] params;
    
	public ParamedException() {
        super();
    }

    public ParamedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamedException(String message) {
        super(message);
    }

    public ParamedException(Throwable cause) {
        super(cause);
    }

    public Object[] getParams() {
		return params;
	}

	public void setParams(Object... params) {
		this.params = params;
	}
}
