package com.mwb.framework.service.aop.validate.field;

import com.mwb.framework.service.aop.validate.field.exception.LoginNameFormatException;
import com.mwb.framework.service.aop.validate.field.exception.ValidationException;
import com.mwb.framework.util.DataValidator;

import java.lang.annotation.Annotation;

public class LoginNameFormatValidator implements IValidator {

	@Override
	public void validate(Object obj, Annotation annotation) throws ValidationException {
		if (obj == null) {
			throw new LoginNameFormatException("Login name is null!");
		}
		
		if (!(obj instanceof String)) {
			throw new LoginNameFormatException("Invalid login name data type!");
		}
		
		String loginName = (String)obj;
		
		if(!DataValidator.isValidEmployeeLoginName(loginName)) {
			String error = String.format("Invalid login name %s.", loginName);
			
			throw new LoginNameFormatException(error);
		}
	}

}
