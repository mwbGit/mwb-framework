package com.mwb.framework.mybatis.proxy;

import com.mwb.framework.log.Log;
import com.mwb.framework.mybatis.BasePackageRetrievableMapperScannerConfigurer;
import com.mwb.framework.mybatis.proxy.injection.ParameterInjectionMapperInvocationHandler;
import com.mwb.framework.util.ReflectionUtility;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class MapperInvocationProxy implements BeanPostProcessor, InvocationHandler {
	private final static Log LOG = Log.getLog(MapperInvocationProxy.class);
	
	private BasePackageRetrievableMapperScannerConfigurer mapperScannerConfigurer;
	
	private Set<Class<?>> mapperInterfaces;
	
	private Map<Class<?>, Object> originalImplementationMap;
	
	private List<PreMapperInvocationHandler> preMapperInvocationHandlers;
	private List<PostMapperInvocationHandler> postMapperInvocationHandlers;
	
	public MapperInvocationProxy(List<PreMapperInvocationHandler> preMapperInvocationHandlers,
			List<PostMapperInvocationHandler> postMapperInvocationHandlers) {
		originalImplementationMap = new HashMap<Class<?>, Object>();
		
		this.preMapperInvocationHandlers = new ArrayList<PreMapperInvocationHandler>();
		this.preMapperInvocationHandlers.add(new ParameterInjectionMapperInvocationHandler());
		
		if (preMapperInvocationHandlers != null) {
			this.preMapperInvocationHandlers.addAll(preMapperInvocationHandlers);
		}
		
		this.postMapperInvocationHandlers = new ArrayList<PostMapperInvocationHandler>();
		
		if (postMapperInvocationHandlers != null) {
			this.postMapperInvocationHandlers.addAll(postMapperInvocationHandlers);
		}
	}
	
	public void init() throws Exception {
		// 获取Mapper接口所在的包名
		String mapperPackage = mapperScannerConfigurer.getBasePackage();
		
		if (StringUtils.isBlank(mapperPackage)) {
			throw new RuntimeException("Blank mapper base package name!");
		}
		
		// 获取所有Mapper接口
		mapperInterfaces = ReflectionUtility.getClassesInPackage(mapperPackage);
		
		Iterator<Class<?>> iterator = mapperInterfaces.iterator();
		while (iterator.hasNext()) {
			Class<?> clazz = iterator.next();
			
			// 判断是否都是接口类
			if (!clazz.isInterface()) {
				throw new RuntimeException("Mapper class should be an interface!");
			}
		}
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		
		for (PreMapperInvocationHandler handler : preMapperInvocationHandlers) {
			handler.register(bean);
		}
		
		for (PostMapperInvocationHandler handler : postMapperInvocationHandlers) {
			handler.register(bean);
		}
		
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		
		// 为每一个Mapper实现生成动态代理
		Iterator<Class<?>> iterator = mapperInterfaces.iterator();
		while (iterator.hasNext()) {
			Class<?> clazz = iterator.next();
			
			if (clazz.isInstance(bean)) {
				LOG.info("Add mapper proxy for {}", beanName);
				
				return newInstance(bean);
			}
		}
		
		return bean;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		LOG.debug("Proxy is invoked - {}", method.getName());
		
		Class<?>[] classes = proxy.getClass().getInterfaces();
		
		if (classes != null && classes.length != 1) {
			throw new RuntimeException("Invalid mapper implmentation - should implement only one interface!");
		}
		
		Object obj = originalImplementationMap.get(classes[0]);
		
		if (obj == null) {
			throw new RuntimeException("No matching mapper implmentation!");
		}
		
		for (PreMapperInvocationHandler handler : preMapperInvocationHandlers) {
			handler.execute(method, args);
		}
		
		Object result = method.invoke(obj, args);
		
		for (PostMapperInvocationHandler handler : postMapperInvocationHandlers) {
			handler.execute(method, args);
		}
		
		return result;
	}

	private Object newInstance(Object obj) {
		Class<?>[] classes = obj.getClass().getInterfaces();
		
		if (classes != null && classes.length != 1) {
			throw new RuntimeException("Invalid mapper implmentation - should implement only one interface!");
		}
		
		originalImplementationMap.put(classes[0], obj);
		
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), this);
	}
	
	public void setMapperScannerConfigurer(
			BasePackageRetrievableMapperScannerConfigurer mapperScannerConfigurer) {
		this.mapperScannerConfigurer = mapperScannerConfigurer;
	}
	
}
