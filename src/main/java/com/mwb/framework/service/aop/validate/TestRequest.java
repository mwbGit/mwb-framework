package com.mwb.framework.service.aop.validate;

import com.mwb.framework.service.aop.validate.field.annotation.MobileFormat;
import com.mwb.framework.service.aop.validate.field.annotation.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class TestRequest {
	@NotNull
	private String name;

	public String getName() {
		return name;
	}

	public void setName(@NotNull Integer id, @MobileFormat(required = true) String mobile) {
		
	}
	
	public static void main(String[] args) throws Exception {
		TestRequest request = new TestRequest();
		
		Class<?> clazz = request.getClass();
		
		Method method = clazz.getDeclaredMethod("setName", Integer.class, String.class);
		
		Annotation[][] annotations = method.getParameterAnnotations();
		
		Annotation[] a = annotations[0];
		
		System.out.println(a);
		/*
		Field field = clazz.getDeclaredField("name");
		
		Annotation[] annotations = field.getAnnotations();
		
		for (Annotation annotation : annotations) {
			System.out.println(annotation.toString());
		} */
	}
}
