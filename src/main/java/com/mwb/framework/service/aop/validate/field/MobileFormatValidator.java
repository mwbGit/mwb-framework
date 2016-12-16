package com.mwb.framework.service.aop.validate.field;

import com.mwb.framework.service.aop.validate.field.annotation.MobileFormat;
import com.mwb.framework.service.aop.validate.field.exception.MobileFormatException;
import com.mwb.framework.service.aop.validate.field.exception.ValidationException;
import com.mwb.framework.util.DataValidator;

import java.lang.annotation.Annotation;

public class MobileFormatValidator implements IValidator {

	@Override
	public void validate(Object obj, Annotation annotation) throws ValidationException {
		if (annotation instanceof MobileFormat) {
			MobileFormat config = (MobileFormat)annotation;
			
			if (obj == null) {
				if (config.required()) {
					throw new MobileFormatException("Mobile is null!"); 
				}
			} else {
				if (!(obj instanceof String)) {
					throw new MobileFormatException("Mobile is not in string format!");
				}
				
				String mobile = (String)obj;

				if(!DataValidator.isValidMobile(mobile)) {
					String error = String.format("Invalid mobile number %s.", mobile);

					throw new MobileFormatException(error);
				}
			}
		} else {
			throw new MobileFormatException("Annotation is not type of MobileFormat!");
		}
	}

}
