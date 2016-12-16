package com.mwb.framework.service.aop.validate;

import com.mwb.framework.service.aop.validate.field.IValidator;
import com.mwb.framework.service.aop.validate.field.exception.ValidationException;

import java.lang.reflect.Method;

public interface IRequestValidator {
	public void validate(Method method, Object[] args) throws ValidationException;
	
	public void registerValdiator(Class<?> validatorAnnotation, IValidator validator);
}
