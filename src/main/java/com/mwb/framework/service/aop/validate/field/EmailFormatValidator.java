package com.mwb.framework.service.aop.validate.field;

import com.mwb.framework.service.aop.validate.field.annotation.EmailFormat;
import com.mwb.framework.service.aop.validate.field.exception.EmailFormatException;
import com.mwb.framework.service.aop.validate.field.exception.ValidationException;
import com.mwb.framework.util.DataValidator;

import java.lang.annotation.Annotation;

public class EmailFormatValidator implements IValidator {

	@Override
	public void validate(Object obj, Annotation annotation) throws ValidationException {
		if (annotation instanceof EmailFormat) {
			EmailFormat config = (EmailFormat)annotation;
			
			if (obj == null) {
				if (config.required()) {
					throw new EmailFormatException("Email is null!"); 
				}
			} else {
				if (!(obj instanceof String)) {
					throw new EmailFormatException("Email is not in string format!");
				}
				
				String email = (String)obj;
				if(!DataValidator.isValidEmail(email)) {
					String error = String.format("Invalid email %s.", email);
					throw new EmailFormatException(error);
				}
			}
		} else {
			throw new EmailFormatException("Annotation is not type of EmailFormat!");
		}
	}

}
