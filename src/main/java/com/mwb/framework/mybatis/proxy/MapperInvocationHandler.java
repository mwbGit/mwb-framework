package com.mwb.framework.mybatis.proxy;

import java.lang.reflect.Method;

public interface MapperInvocationHandler {
	public void execute(Method method, Object[] args) throws Throwable;
	
	public void register(Object bean);
}
