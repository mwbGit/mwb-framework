package com.mwb.framework.service;

import com.alibaba.fastjson.JSON;
import com.mwb.framework.api.service.rs.api.ServiceResponse;
import com.mwb.framework.log.Log;
import com.mwb.framework.service.api.exception.IExceptionMapper;
import com.mwb.framework.service.api.exception.ParamedException;
import com.mwb.framework.util.StringUtility;
import nu.xom.*;
import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class StatusCodeManager implements ResourceLoaderAware, InitializingBean, BeanPostProcessor {
	
	private final static Log LOG = Log.getLog(StatusCodeManager.class);

	private static String SUCCEED_CODE = "100000";
	private static String SYSTEM_ERROR_CODE = "100001";
	//如果依赖framework 1.31及以上， 那么须依赖module 1.0
	private static String[] DEFAULT_CODE_CONFIG = new String[]{"classpath:/code/default", "classpath:/code/module"};
	
	private static String[] DEFAULT_EXCEPTION_CONFIG = new String[]{"classpath:/code/default.xml", "classpath:/code/module.xml"};
	
	private final static String ATTRIBUTE_CLASS = "class";
	
	private final static String ATTRIBUTE_CODE = "code";
	
	private final static String ATTRIBUTE_STATUS = "status";
	
	private final static Set<String> ALL_CODES = new HashSet<String>();

	private final static Map<String, ResponseInfomation> EXCEPTION_CONVERTER = new HashMap<String, ResponseInfomation>();
	
	private int defaultStatus = 200;
	
	private Element root;
	
	private ResourceLoader resourceLoader;
	private String configFile;
	
	private static ReloadableResourceBundleMessageSource messageSource;

	private String[] basenames;
	
	@SuppressWarnings("rawtypes")
	private final static Map<String, IExceptionMapper> EXCEPTION_MAPPER = new HashMap<String, IExceptionMapper>();
	
	public static String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, null);
    }

	public static String getSucceedCode() {
		return SUCCEED_CODE;
	}

	public static String getSystemErrorCode() {
		return SYSTEM_ERROR_CODE;
	}
	
	public static String getSucceedMessage() {
		return messageSource.getMessage(SUCCEED_CODE, null, null);
	}
	
	public static boolean contains(String code) {
		return ALL_CODES.contains(code);
	}
	
	@SuppressWarnings("unchecked")
	public static Exception mappingException(Exception e) {
		@SuppressWarnings("rawtypes")
		IExceptionMapper mapper = EXCEPTION_MAPPER.get(e.getClass().getName());
		if (mapper != null) {
			e = mapper.mapping(e);
		}
		
		return e;
	}
	
	public static ResponseInfomation resolveException(Exception e) {
		LOG.debug("Handle exception!", e);
		
		ResponseInfomation responseInfomation = null;
		
		Class<?> clazz = e.getClass();
		
		while (clazz!=null && !clazz.getName().equals("java.lang.Object")) {
			
			responseInfomation = EXCEPTION_CONVERTER.get(clazz.getName());
			
			if (responseInfomation!=null) {
				break;
			}
			
			clazz = clazz.getSuperclass();
		}
		
		return responseInfomation;
	}
	
	public static String getJsonErrorResponse(Exception e) {
		e = StatusCodeManager.mappingException(e);
		
		ResponseInfomation responseInfomation = resolveException(e);
		
		ServiceResponse response  = new ServiceResponse();
		response.setResultCode(responseInfomation.getResponseCode());
		
		String message;
        if (e instanceof ParamedException) {
        	message = responseInfomation.getLocalizedResponseMessage(((ParamedException)e).getParams());
        } else {
        	message = responseInfomation.getLocalizedResponseMessage();
        }
        response.setResultMessage(message);
		
		return JSON.toJSONString(response);
	}
	
	public static Response toResponse(Exception exception) {
		exception = StatusCodeManager.mappingException(exception);
		
		ResponseInfomation responseInfomation = resolveException(exception);
		
		ServiceResponse response  = new ServiceResponse();
		response.setResultCode(responseInfomation.getResponseCode());
		
		String message;
        if (exception instanceof ParamedException) {
        	message = responseInfomation.getLocalizedResponseMessage(((ParamedException)exception).getParams());
        } else {
        	message = responseInfomation.getLocalizedResponseMessage();
        }
        response.setResultMessage(message);
        
        String errorId = getErrorId();	      
        response.setErrorId(errorId);
        
        String errorMsg = String.format("[ErrorId %s] %s", errorId, exception.getMessage());
     	
        if (StatusCodeManager.getSystemErrorCode().equals(response.getResultCode())) {
        	LOG.error(errorMsg, exception);				
        } else {
        	LOG.error("{} \n {}", errorMsg, StringUtility.getSimpleStackTrace(exception));
        }

		ResponseBuilderImpl builder = new ResponseBuilderImpl();
		
		builder.status(responseInfomation.getStatusCode());
		builder.entity(response);
		
		return builder.build();
	}
	
	private static String getErrorId() {
		long timestamp = System.currentTimeMillis();
		
		return "E" + String.valueOf(timestamp % 10000000);
	}
	
	private void parse(Class<?> parent, Element root) {
		String name = root.getAttribute(ATTRIBUTE_CLASS).getValue();
		String code = root.getAttribute(ATTRIBUTE_CODE).getValue();
		Attribute statusAttribute = root.getAttribute(ATTRIBUTE_STATUS);

		if (!StatusCodeManager.contains(code)) {
			String cause = String.format("Undefined status code %s.", code);
			LOG.error(cause);
			throw new RuntimeException(cause);
		}
		
		int status = defaultStatus;
		if (statusAttribute != null) {
			int statusValue = Integer.valueOf(statusAttribute.getValue());
			if (statusValue < 0) {
				throw new IllegalArgumentException(
						String.format(
								"The value of status is illegal for name %s and code %s.",
								name, code));
			}
			
			status = statusValue;
		}
		
		//verify exception inheritance relationship
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
	
		try {
			Class<?> clazz = cl.loadClass(name);
			
			if (!parent.isAssignableFrom(clazz)) {
				String cause = String.format("Error configuration for exception %s.", name);
				LOG.error(cause);
				throw new RuntimeException(cause);
			}
			
			ResponseInfomation responseInfomation = new ResponseInfomation(code, status);
			
			if ((EXCEPTION_CONVERTER.containsKey(name) && !EXCEPTION_CONVERTER.containsValue(responseInfomation))
				|| (!EXCEPTION_CONVERTER.containsKey(name) && EXCEPTION_CONVERTER.containsValue(responseInfomation))) {
				
				String cause = String.format("Inconsistent code [%s] and exception [%s].", code, name);
				LOG.error(cause);
				throw new RuntimeException(cause);
			} else if (!EXCEPTION_CONVERTER.containsKey(name) && !EXCEPTION_CONVERTER.containsValue(responseInfomation)) {
				EXCEPTION_CONVERTER.put(name, responseInfomation);
			}
			
			Elements children = root.getChildElements();
			for (int i = 0; i < children.size(); i++) {
				Element element = children.get(i);
				parse(clazz, element);
			}
		} catch (ClassNotFoundException e) {
			LOG.error("Not find exception class {}!", name);
		}
	}
	
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public void setBasenames(String[] basenames) {
		this.basenames = basenames;
	}
	
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		loadResource();
		parseDefaultException();
		parseException();
	}
	
	private void loadResource() throws Exception {
		messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setDefaultEncoding("UTF-8");
		
		if (basenames != null && basenames.length > 0) {
			String[] names = new String[basenames.length + 2];
			
			names[0] = DEFAULT_CODE_CONFIG[0];
			
			names[1] = DEFAULT_CODE_CONFIG[1];
			
			for (int i = 0; i < basenames.length; i ++) {
				names[i + 2] = basenames[i];
			}
			
			String[] resultNames = parseCodes(names);
			
			messageSource.setBasenames(resultNames);
		} else {
			String[] resultNames = parseCodes(DEFAULT_CODE_CONFIG);
			
			messageSource.setBasenames(resultNames);
		}
	}
	
	private String[] parseCodes(String[] basenames) throws Exception {
		List<String> list = new ArrayList<String>();
		Collections.addAll(list, basenames);
		
		for (String basename : basenames) {
			String propertyFile = basename + ".properties";

			Resource resource = resourceLoader.getResource(propertyFile);
			
			InputStream is = null;
			try {
				is = resource.getInputStream();
			} catch (IOException e) {
				LOG.info(" resource {} not found ", propertyFile);
			}

			if (is == null) {
				list.remove(basename);
				continue;
			}
			
			Properties prop = new Properties();
			
			prop.load(is);
			
			Enumeration<Object> keys = prop.keys();
			while (keys.hasMoreElements()) {
				String code = (String)keys.nextElement();
				
				if (!ALL_CODES.contains(code)) {
					ALL_CODES.add(code);
				} else {
					String cause = String.format("Duplicate code [%s].", code);
					LOG.error(cause);
					throw new RuntimeException(cause);
				}
			}
		}
		return list.toArray(new String[]{});
	}
	
	private void parseDefaultException() throws Exception {
		for (String config : DEFAULT_EXCEPTION_CONFIG) {
			Resource resource = resourceLoader.getResource(config);

			InputStream is = null;
			try {
				is = resource.getInputStream();
			} catch (IOException e) {
				LOG.info(" resource {} not found ", config);
			}

			if (is == null) {
				continue;
			}

			Builder parser = new Builder();
			Document doc = parser.build(is);

			root = doc.getRootElement();
			parse(Throwable.class, root);
		}
	}
	
	private void parseException() throws Exception {
		
		Resource resource = resourceLoader.getResource(configFile);
		
		InputStream is = resource.getInputStream();

		if (is == null) {
			throw new RuntimeException("Cannot load exception configuration file!");
		}

		Builder parser = new Builder();
		Document doc = parser.build(is);

		root = doc.getRootElement();
		parse(Throwable.class, root);

	}
	
	public class ResponseInfomation {
		final String responseCode;
		final int statusCode;
		
		public ResponseInfomation(String responseCode, int statusCode) {
			this.responseCode = responseCode;
			this.statusCode = statusCode;
		}

		public String getResponseCode() {
			return responseCode;
		}
		
		public int getStatusCode() {
			return statusCode;
		}
		
		public String getLocalizedResponseMessage(Object... args) {
			return StatusCodeManager.getMessage(responseCode, args);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((responseCode == null) ? 0 : responseCode.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ResponseInfomation other = (ResponseInfomation) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (responseCode == null) {
				if (other.responseCode != null)
					return false;
			} else if (!responseCode.equals(other.responseCode))
				return false;
			return true;
		}

		private StatusCodeManager getOuterType() {
			return StatusCodeManager.this;
		}
		
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof IExceptionMapper) {
			IExceptionMapper<?> mapper = (IExceptionMapper<?>)bean;
			registerMapper(mapper);
		}

		return bean;
	}

	private void registerMapper(IExceptionMapper<?> mapper) {
		String className = mapper.getClassName();
		
		if (EXCEPTION_MAPPER.keySet().contains(className)) {
			String error = String.format("Duplicate exception mapper %s instance!", className);

			throw new RuntimeException(error);
		}

		EXCEPTION_MAPPER.put(className, mapper);
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

}
