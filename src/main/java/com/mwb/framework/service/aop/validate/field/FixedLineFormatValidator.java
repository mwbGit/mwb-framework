package com.mwb.framework.service.aop.validate.field;

import com.mwb.framework.service.aop.validate.field.annotation.FixedLineFormat;
import com.mwb.framework.service.aop.validate.field.exception.FixedLineFormatException;
import com.mwb.framework.service.aop.validate.field.exception.ValidationException;
import com.mwb.framework.util.DataValidator;
import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

public class FixedLineFormatValidator implements IValidator {

	@Override
	public void validate(Object obj, Annotation annotation) throws ValidationException {
		if (obj == null || !(obj instanceof Map<?, ?>)) {
			throw new FixedLineFormatException();
		}
		
		Map<?, ?> fields = (Map<?, ?>)obj;
		
		Object f1 = fields.get(FixedLineFormat.REGION_CODE);
		Object f2 = fields.get(FixedLineFormat.PHONE_NUMBER);
		Object f3 = fields.get(FixedLineFormat.EXTENSION);
		
		String regionCode = getStringValue(f1);
		String phoneNumber = getStringValue(f2);
		String extension = getStringValue(f3);
		
		if (StringUtils.isNotBlank(regionCode) 
				|| StringUtils.isNotBlank(phoneNumber)
                || StringUtils.isNotBlank(extension)) {
            
			if (!DataValidator.isValidFixedLine(regionCode, phoneNumber, extension)) {
            	String error = String.format("Invalid fixed line %s-%s-%s!", regionCode, phoneNumber, extension);
    			
    			throw new FixedLineFormatException(error);
            }
        }
	}

	private String getStringValue(Object obj) throws ValidationException {
		if (obj == null) {
			return null;
		}
		
		if (!(obj instanceof String)) {
			throw new FixedLineFormatException();
		}
		
		return (String)obj;
	}
}
