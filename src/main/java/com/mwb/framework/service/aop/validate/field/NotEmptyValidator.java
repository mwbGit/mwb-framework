package com.mwb.framework.service.aop.validate.field;

import com.mwb.framework.service.aop.validate.field.exception.NotEmptyException;

import java.lang.annotation.Annotation;
import java.util.List;

public class NotEmptyValidator implements IValidator {

	@Override
	public void validate(Object obj, Annotation annotation) throws NotEmptyException {
		if (obj == null) {
			throw new NotEmptyException("Value is null!");
		}
		
		// 暂时不需要考虑Array和Map的类型，因为不会出现在Rest Request里面
		if (obj instanceof List<?>) {
			List<?> listObj = (List<?>)obj;
			
			if (listObj.size() < 1) {
				throw new NotEmptyException("Value is empty!");
			}
		}
	}
}
