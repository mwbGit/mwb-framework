package com.mwb.framework.mybatis.proxy.injection;

import com.mwb.framework.mybatis.proxy.PreMapperInvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ParameterInjectionMapperInvocationHandler implements PreMapperInvocationHandler {
	private Map<Class<?>, IObjectInjector> objectInjectorMap;
	
	public ParameterInjectionMapperInvocationHandler() {
		objectInjectorMap = new HashMap<Class<?>, IObjectInjector>();
	}
	
	@Override
	public void register(Object bean) {
		// 生成object injector和类型的map 
		if (bean instanceof IObjectInjector) {
			IObjectInjector injector = (IObjectInjector)bean;
			
			if (objectInjectorMap.get(injector.getClass()) != null) {
				throw new RuntimeException("Has duplicate IObjectInjector type!");
			}
			
			objectInjectorMap.put(injector.getClass(), injector);
		}
	}

	@Override
	public void execute(Method method, Object[] args) throws Throwable {
		
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		
		for (int i = 0; i < parameterAnnotations.length; i ++) {
			Annotation[] annotations = parameterAnnotations[i];
			
			if (annotations != null && annotations.length > 0) {
				for (int j = 0; j < annotations.length; j ++) {
					Annotation annotation = annotations[j];

					// 判断调用参数上是否有Injection的Annotation
					if (Injection.class.equals(annotation.annotationType())) {
						Injection injection = (Injection)annotation;
						
						args[i] = getInjectedObject(injection.value(), args[i]);						
					}
				}
			}
		}
	}

	private Object getInjectedObject(Class<?> type, Object originalObj) {
		IObjectInjector injector = objectInjectorMap.get(type);
		
		if (injector != null) {
			return injector.getObject(originalObj);
		}
		
		return originalObj;
	}
}
