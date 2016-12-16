package com.mwb.framework.service.aop.validate.field;

import com.mwb.framework.service.aop.validate.field.exception.PasswordFormatException;
import com.mwb.framework.service.aop.validate.field.exception.ValidationException;
import com.mwb.framework.util.DataValidator;

import java.lang.annotation.Annotation;

public class PasswordFormatValidator implements IValidator {

	@Override
	public void validate(Object obj, Annotation annotation) throws ValidationException {
		if (obj == null || !(obj instanceof String)) {
			throw new PasswordFormatException();
		}
		
		String password = (String)obj;
		
		if(!DataValidator.isValidEmployeePassword(password)) {
			
			throw new PasswordFormatException("Invalid password");
		}
	}

}
