package com.mwb.framework.service.aop.validate.field.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FixedLineFormat {
	public static final String REGION_CODE = "regionCode";
	public static final String PHONE_NUMBER = "phoneNumber";
	public static final String EXTENSION = "extension";
	
	public static final boolean multiFields = true;
	
	public int groupId();
	public String fieldName();
	
}
