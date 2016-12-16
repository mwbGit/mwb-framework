package com.mwb.framework.service.aop;

import com.mwb.framework.service.aop.normalize.IRequestNormalizer;
import com.mwb.framework.service.aop.security.IServiceInvocationAuthorizer;
import com.mwb.framework.service.aop.validate.IRequestValidator;

import java.lang.reflect.Method;

public class PreRestServiceHandler {

	private IServiceInvocationAuthorizer authorizer;
	
	private IRequestNormalizer normalizer;
	
	private IRequestValidator validator;
	
	public Object[] process(Method method, Object [] args) throws Exception {
		
		authorizer.authorize(method);
		
		Object[] newArgs = normalizer.normalize(method, args);
		
		validator.validate(method, newArgs);
		
		return newArgs;
	}

	public void setAuthorizer(IServiceInvocationAuthorizer authorizer) {
		this.authorizer = authorizer;
	}

	public void setNormalizer(IRequestNormalizer normalizer) {
		this.normalizer = normalizer;
	}

	public void setRequestValidator(IRequestValidator validator) {
		this.validator = validator;
	}
}
