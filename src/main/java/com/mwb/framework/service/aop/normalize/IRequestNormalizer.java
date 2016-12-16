package com.mwb.framework.service.aop.normalize;

import java.lang.reflect.Method;

public interface IRequestNormalizer {
	public Object[] normalize(Method method, Object[] args) throws RequestNormalizeException;
}
