package com.mwb.framework.service.api.exception;

public interface IExceptionMapper<T extends Exception> {
	Exception mapping(T e);
	String getClassName();
}
