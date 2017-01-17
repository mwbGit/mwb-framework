package com.mwb.framework.service.aop.validate;

import com.mwb.framework.api.model.PagingData;
import com.mwb.framework.api.service.aop.annotation.IgnoreValidate;
import com.mwb.framework.api.service.rs.api.PagingRequest;
import com.mwb.framework.log.Log;
import com.mwb.framework.service.aop.PreRestServiceHandler;
import com.mwb.framework.service.aop.validate.field.*;
import com.mwb.framework.service.aop.validate.field.annotation.*;
import com.mwb.framework.service.aop.validate.field.exception.PagingRequestFormatException;
import com.mwb.framework.service.aop.validate.field.exception.ValidationException;
import com.mwb.framework.util.ReflectionUtility;
import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

// 对分页请求数据，以及带validation annotation的参数进行验证
public class RequestValidator implements IRequestValidator {
	private final static Log LOG = Log.getLog(RequestValidator.class);
	
	private final static String MULTI_FIELDS = "multiFields";
	private final static String GROUP_ID = "groupId";
	private final static String FIELD_NAME = "fieldName";
	
	private final static String PACKAGE_BASE;
	
	static {
		Package pack = PreRestServiceHandler.class.getPackage();
		
		String packageName = pack.getName();
		
		String[] packagePaths = packageName.split("\\.");
		
		PACKAGE_BASE = packagePaths[0] + "." + packagePaths[1];
	}
	
	private Map<Class<?>, IValidator> validatorMap;
	
	public RequestValidator() {
		initValidatorMap();
	}
	
	@Override
	public void validate(Method method, Object[] args) throws ValidationException {
		Map<Object, ValidationData> validationDataMap = new HashMap<Object, ValidationData>();
		
		if (method != null && (args != null && args.length > 0)) {
			
			Annotation[][] parameterAnnotations = ReflectionUtility.getOverridedParameterAnnotations(method);
			
			for (int i = 0; i < args.length; i ++) {
				Object arg = args[i];
				
				if (arg instanceof PagingRequest) {
					// 验证分页请求
					validatePagingRequest((PagingRequest)arg);
				}
				
				Annotation[] annotations = parameterAnnotations[i];
				
				// 是否不做参数验证
				boolean ignored = false;
				for (Annotation annotation : annotations) {
					if (IgnoreValidate.class.getName().equals(annotation.annotationType().getName())) {
						ignored = true;
						break;
					}
				}
				
				if (ignored) {
					continue;
				}
				
				
				// 验证方法级别的参数
				for (Annotation annotation : annotations) {
					if (isValidatorAnnotation(annotation.annotationType())) {
						addValidatorData(annotation, arg.getClass().getSimpleName(), arg, validationDataMap);
					}
				}
				
				// 递归验证对象里的参数
				try {
					validate(arg, validationDataMap);
				} catch (Exception e) {
					throw new ValidationException(e);
				}
			}
		}
		
		for (ValidationData validationData :validationDataMap.values()) {
			Annotation annotation = validationData.getAnnotation();
			
			IValidator validator = validatorMap.get(annotation.annotationType());
			if (validator != null) {
				try {
					validator.validate(validationData.getFieldValue(), annotation);
				} catch (ValidationException e) {
					LOG.error("Catch a ValidationException - [field: {}, message: {}]", 
							validationData.getFieldName(), e.getMessage());
					
					throw e;
				}
			}
		}
		
	}

	private void validate(Object obj, Map<Object, ValidationData> validationDataMap) throws Exception {
		if (obj != null && isInternalDefinedClass(obj.getClass().getPackage())) {
			Class<?> clazz = obj.getClass();
			
			Field[] fields = clazz.getDeclaredFields();
			
			for (Field field : fields) {
				field.setAccessible(true);
				
				Annotation[] annotations = field.getDeclaredAnnotations();
				
				for (Annotation annotation : annotations) {
					if (isValidatorAnnotation(annotation.annotationType())) {
						addValidatorData(annotation, field.getName(), field.get(obj), validationDataMap);
					}
				}
			}
			
			Method[] methods  = clazz.getMethods();
			
			for (Method method : methods) {
				String methodName = method.getName();
				
				if (methodName != null && methodName.startsWith("get")
						&& method.getParameterTypes().length == 0) {
					validate(method.invoke(obj), validationDataMap);
				}
			}
		}
	}
	
	private boolean isValidatorAnnotation(Class<? extends Annotation> annotation) {
		return validatorMap.containsKey(annotation);
	}
	
	private void addValidatorData(Annotation annotation, String fieldName, Object fieldValue,
			Map<Object, ValidationData> validationDataMap) {
		
		try {
			Class<?> clazz = annotation.getClass();
			Field field = clazz.getField(MULTI_FIELDS);
			Boolean multiFields = (Boolean)field.get(annotation);

			if (multiFields) {
				Method method = clazz.getMethod(GROUP_ID);
				Integer groupId = (Integer)method.invoke(annotation);
				
				method = clazz.getMethod(FIELD_NAME);
				String fName = (String)method.invoke(annotation);
				
				String key = annotation.annotationType().getSimpleName() + "_" + groupId;
				
				ValidationData validataionData = validationDataMap.get(key);
				if (validataionData == null) {
					validataionData = new ValidationData();
					
					validataionData.setAnnotation(annotation);
					validataionData.setFieldValue(new HashMap<String, Object>());
					
					validationDataMap.put(key, validataionData);
				}
				
				String allFieldName = validataionData.getFieldName();
				if (StringUtils.isBlank(allFieldName)) {
					allFieldName = fieldName;
				} else {
					allFieldName = allFieldName + ", " + fieldName;
				}
				
				validataionData.setFieldName(allFieldName);
				
				@SuppressWarnings("unchecked")
				Map<String, Object> multiFieldMap = (Map<String, Object>)validataionData.getFieldValue();
				
				multiFieldMap.put(fName, fieldValue);
			} else {
				ValidationData validataionData = new ValidationData();
				validataionData.setAnnotation(annotation);
				validataionData.setFieldName(fieldName);
				validataionData.setFieldValue(fieldValue);
				
				validationDataMap.put(new Object(), validataionData);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private void initValidatorMap() {
		validatorMap = new HashMap<Class<?>, IValidator>();
		
		validatorMap.put(NotNull.class, new NotNullValidator());
		validatorMap.put(NotEmpty.class, new NotEmptyValidator());
		validatorMap.put(EmailFormat.class, new EmailFormatValidator());
		validatorMap.put(MobileFormat.class, new MobileFormatValidator());
		validatorMap.put(LoginNameFormat.class, new LoginNameFormatValidator());
		validatorMap.put(PasswordFormat.class, new PasswordFormatValidator());
		validatorMap.put(FixedLineFormat.class, new FixedLineFormatValidator());
		validatorMap.put(NumericalRangeFormat.class, new NumericalRangeValidator());
	}
	
	@Override
	public void registerValdiator(Class<?> validatorAnnotation, IValidator validator) {
		IValidator existingValidator = validatorMap.get(validatorAnnotation);
		if (existingValidator != null) {
			throw new RuntimeException("Duplicate validator!");
		}
		
		validatorMap.put(validatorAnnotation, validator);
	}

	private void validatePagingRequest(PagingRequest request) throws ValidationException {
		if (request.isPaged()) {
			int pageNumber = request.getPageNumber();
			int pageSize = request.getPageSize();
			
			if (pageNumber <= 0 || pageSize <= 0 || pageSize > PagingData.MAX_PAGE_SIZE) {
				String error = String.format("Invalid paging request - [pageNumber: %s, pageSize: %s]", pageNumber, pageSize);
				
				throw new PagingRequestFormatException(error);
			}
		}
	}
	
	private boolean isInternalDefinedClass(Package pack) {
		return (pack != null && pack.getName().startsWith(PACKAGE_BASE));
	}

	private class ValidationData {
		private Annotation annotation;
		
		private String fieldName;
		private Object fieldValue;
		
		public Annotation getAnnotation() {
			return annotation;
		}

		public void setAnnotation(Annotation annotation) {
			this.annotation = annotation;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public Object getFieldValue() {
			return fieldValue;
		}

		public void setFieldValue(Object fieldValue) {
			this.fieldValue = fieldValue;
		}
		
	}
}
