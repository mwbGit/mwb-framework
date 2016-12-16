package com.mwb.framework.service.aop.normalize;

import com.mwb.framework.api.common.service.aop.annotation.IgnoreNormalize;
import com.mwb.framework.log.Log;
import com.mwb.framework.service.aop.PreRestServiceHandler;
import com.mwb.framework.util.DataValidator;
import com.mwb.framework.util.ReflectionUtility;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RequestNormalizer implements IRequestNormalizer {

	private final static Log LOG = Log.getLog(RequestNormalizer.class);
	
	private final static String PACKAGE_BASE;
	
	static {
		Package pack = PreRestServiceHandler.class.getPackage();
		
		String packageName = pack.getName();
		
		String[] packagePaths = packageName.split("\\.");
		
		PACKAGE_BASE = packagePaths[0] + "." + packagePaths[1];
	}
	
	@Override
	public Object[] normalize(Method method, Object[] args) throws RequestNormalizeException {
		
		try {
			if (method != null && args != null) {
				
				Annotation[][] parameterAnnotations = ReflectionUtility.getOverridedParameterAnnotations(method);
				
				Object[] newArgs = new Object[args.length];
				
				ARGS: 
				for (int i = 0; i < args.length; i++) {

					Object arg = args[i];
					
					if (arg != null) {
						Annotation[] annotations = parameterAnnotations[i];
						if (annotations != null) {
							for (Annotation annotation : annotations) {
								if (IgnoreNormalize.class.equals(annotation.annotationType())) {
									newArgs[i] = arg;
									continue ARGS;
								}
							}
						}

						Class<?> clazz = arg.getClass();
						if (String.class.equals(clazz)) {
							newArgs[i] = nomalize((String) arg);
						} else if (Integer.class.equals(clazz)) {
							newArgs[i] = nomalize((Integer) arg);
						} else if (Long.class.equals(clazz)) {
							newArgs[i] = nomalize((Long) arg);
						} else if (isInternalDefinedClass(clazz.getPackage())) {
							newArgs[i] = nomalize(arg);
						} else {
							newArgs[i] = arg;
						}
					} else {
						newArgs[i] = arg;
					}
				}

				return newArgs;
			
			} else {
				return null;
			}
			
		} catch (Exception e) {
			LOG.error("Catch an exception!", e);
			
			throw new RequestNormalizeException(e);
		}
	}
	
	private String nomalize(String value) {
		// 如果值是 "", "null", "undefined", 或"NONE" 统一设置为null
		if (DataValidator.isEmptyData(value)) {
			return null;
		} 
		
		return value;
	}
	
	private Integer nomalize(Integer value) {
		// 如果值小于0统一设置为null
		if (value != null && value < 0) {
			return null;
		} 
		
		return value;
	}
	
	private Long nomalize(Long value) {
		// 如果值小于0统一设置为null
		if (value != null && value < 0) {
			return null;
		} 
		
		return value;
	}
	
	private Object nomalize(Object obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (obj != null) {
			Class<?> clazz = obj.getClass();
			
			Method[] methods  = clazz.getMethods();
	
			Map<String, Method> getterMethodMap = new HashMap<String, Method>();
			Map<String, Method> setterMethodMap = new HashMap<String, Method>();
			for (Method method : methods) {
				if (method.getAnnotation(IgnoreNormalize.class) != null) {
					continue;
				}
				
				String methodName = method.getName();
				
				if (methodName != null && methodName.startsWith("get")) {
					getterMethodMap.put(methodName.substring(3), method);
				} else if (methodName != null && methodName.startsWith("set")) {
					setterMethodMap.put(methodName.substring(3), method);
				}
			}
			
			Set<Entry<String, Method>> entrySet = getterMethodMap.entrySet();
			Iterator<Entry<String, Method>> iterator = entrySet.iterator();
			while (iterator.hasNext()) {
				Entry<String, Method> entry = iterator.next();
				
				String fieldName = entry.getKey();
				Method getterMethod = entry.getValue();
				Class<?> returnType = getterMethod.getReturnType();
				
				Method setterMethod = setterMethodMap.get(fieldName);
				
				if (setterMethod != null) {
					
					if (String.class.equals(returnType)) {
						// 如果类型是String
						Object fieldValue = getterMethod.invoke(obj);
	
						if (nomalize((String)fieldValue) == null) {
							setterMethod.invoke(obj, (String)null);
						}
					} else if (Integer.class.equals(returnType)) {
						// 如果类型是Integer
						Object fieldValue = getterMethod.invoke(obj);
						
						if (nomalize((Integer)fieldValue) == null) {
							setterMethod.invoke(obj, (Integer)null);
						}
					} else if (Long.class.equals(returnType)) {
						// 如果类型是Integer
						Object fieldValue = getterMethod.invoke(obj);
						
						if (nomalize((Long)fieldValue) == null) {
							setterMethod.invoke(obj, (Long)null);
						}
					} else {
						// 只检查自己定义的对象类型
						if (isInternalDefinedClass(returnType.getPackage())) {
							Object fieldValue = getterMethod.invoke(obj);
							nomalize(fieldValue);
						}
					}
				}
			}
		}
		
		return obj;
	}
	
	private boolean isInternalDefinedClass(Package pack) {
		return (pack != null && pack.getName().startsWith(PACKAGE_BASE));
	}
	


}
