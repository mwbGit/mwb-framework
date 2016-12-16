package com.mwb.framework.service.aop.validate.field;

import com.mwb.framework.service.aop.validate.field.annotation.NumericalRangeFormat;
import com.mwb.framework.service.aop.validate.field.exception.NumericalRangeFormatException;
import com.mwb.framework.service.aop.validate.field.exception.ValidationException;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;

public class NumericalRangeValidator implements IValidator {

	@Override
	public void validate(Object obj, Annotation annotation) throws ValidationException {
		if (annotation instanceof NumericalRangeFormat) {
			NumericalRangeFormat config = (NumericalRangeFormat)annotation;
			
			if (obj == null) {
				throw new NumericalRangeFormatException("Field is null!"); 
			} else {
				Double value = null;
				try {
					if (obj instanceof String) {
						value = Double.valueOf((String)obj);
					} else if (obj instanceof Double) {
						value = (Double)obj;
					} else if (obj instanceof Integer) {
						value = BigDecimal.valueOf((Integer)obj).doubleValue();
					} else if (obj instanceof Long) {
						value = BigDecimal.valueOf((Long)obj).doubleValue();
					} else if (obj instanceof BigInteger) {
						value = ((BigInteger)obj).doubleValue();
					} else if (obj instanceof BigDecimal) {
						value = ((BigDecimal)obj).doubleValue();
					} else {
						throw new NumericalRangeFormatException("Field is not a numerical Object."); 
					}
				} catch (Exception e) {
					throw new NumericalRangeFormatException("Fail to parse the value.", e); 
				}
				
				double min = config.min();
				double max = config.max();
				
				if (value < min || value > max) {
					throw new NumericalRangeFormatException("Field is not a numerical Object."); 
				}
			}
		} else {
			throw new NumericalRangeFormatException("Annotation is not type of NumericalRangeFormat!");
		}
	}
}
