package com.mwb.framework.service.aop;

import com.mwb.framework.api.common.service.rs.api.ServiceResponse;
import com.mwb.framework.datasource.ReadOnlyDS;
import com.mwb.framework.datasource.ReadOnlyDataSourceHolder;
import com.mwb.framework.log.Log;
import com.mwb.framework.service.StatusCodeManager;
import com.mwb.framework.service.aop.handler.DefaultRestServiceProcessor;
import com.mwb.framework.service.aop.handler.IRestServiceProcessor;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class RestServiceAspect implements BeanPostProcessor {
	private final static Log LOG = Log.getLog(RestServiceAspect.class);

	private PreRestServiceHandler preRestServiceHandler;

	private final SortedSet<IRestServiceProcessor> REST_SERVICE_PROCESSORS;


	public RestServiceAspect() {
		REST_SERVICE_PROCESSORS = new TreeSet<IRestServiceProcessor>(new Comparator<IRestServiceProcessor>() {
			//按照优先级 降序排列
			@Override
			public int compare(IRestServiceProcessor handler1, IRestServiceProcessor handler2) {
				return handler2.sequence().compareTo(handler1.sequence());
			}
		});

		REST_SERVICE_PROCESSORS.add(new DefaultRestServiceProcessor());
	}

	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		long time = System.currentTimeMillis();

		MethodSignature msig = (MethodSignature)pjp.getSignature();

		try {
			if (LOG.isDebugEnabled()) {
				String [] argNames = msig.getParameterNames();
				Object [] argValues = pjp.getArgs();

				StringBuffer sb = new StringBuffer();
				sb.append(pjp.getTarget().getClass().getName())
						.append(".").append(pjp.getSignature().getName())
						.append(" input parameters: ");

				if (argNames != null && argNames.length > 0) {
					for (int i=0; i<argNames.length; i++) {
						sb.append(argNames[i]).append("=").append(argValues[i]).append(" ");
					}
				}

				LOG.debug(sb.toString());
			}
		} catch (Exception e) {
			LOG.error("Failed to log input paramenters and values", e);
		}

		ServiceResponse serviceResponse;
		try {
			Method method = msig.getMethod();

			Object[] args = preRestServiceHandler.process(method, pjp.getArgs());

			ReadOnlyDS dataSource = method.getAnnotation(ReadOnlyDS.class);

			if (dataSource != null) {
				ReadOnlyDataSourceHolder.setReadOnly();
			}

			serviceResponse = proceed(pjp, args);

			if (StringUtils.isBlank(serviceResponse.getResultCode())) {
				serviceResponse.setResultCode(StatusCodeManager.getSucceedCode());
				serviceResponse.setResultMessage(StatusCodeManager.getSucceedMessage());
			} else {
				if (StringUtils.isBlank(serviceResponse.getResultMessage())) {
					serviceResponse.setResultMessage(StatusCodeManager.getMessage(serviceResponse.getResultCode()));
				}
			}
		} finally {
			ReadOnlyDataSourceHolder.clear();
		}

		try {
			long cost = System.currentTimeMillis() - time;
			if (cost > 5000) {
				String[] argNames = msig.getParameterNames();
				Object[] argValues = pjp.getArgs();

				StringBuffer sb = new StringBuffer();
				sb.append("[LONG CALL]");
				sb.append(pjp.getTarget().getClass().getName()).append(".")
						.append(pjp.getSignature().getName())
						.append(" {input parameters}: ");

				if (argNames != null && argNames.length > 0) {
					for (int i = 0; i < argNames.length; i++) {
						sb.append(argNames[i]).append("=").append(argValues[i])
								.append(" ");
					}
				}

				sb.append(". {response}: ").append(serviceResponse);
				sb.append(". {cost}: ").append(cost).append("ms");
				LOG.warn(sb.toString());
			}
		} catch (Exception e) {
			LOG.error("Failed to log long call.", e);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(serviceResponse.toString());

			LOG.debug("Method {}.{} return: {}.", new Object[] {
					pjp.getTarget().getClass().getName(), pjp.getSignature().getName(), serviceResponse});

			time = System.currentTimeMillis() - time;
			LOG.debug("Method {}.{} cost {} ms.", new Object[] {
					pjp.getTarget().getClass().getName(), pjp.getSignature().getName(), time});
		}

		return serviceResponse;
	}

	public void setPreRestServiceHandler(PreRestServiceHandler preRestServiceHandler) {
		this.preRestServiceHandler = preRestServiceHandler;
	}

	private ServiceResponse proceed(ProceedingJoinPoint pjp, Object[] args) throws Throwable {

		for (IRestServiceProcessor handler : REST_SERVICE_PROCESSORS) {
			if (handler.match()) {
				return handler.process(pjp, args);
			}
		}

		throw new RuntimeException("rest service default handler error");
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof IRestServiceProcessor) {
			IRestServiceProcessor handler = (IRestServiceProcessor)bean;
			registerHandler(handler);
		}
		return bean;
	}

	private void registerHandler(IRestServiceProcessor handler) {

		if (REST_SERVICE_PROCESSORS.contains(handler)) {
			String error = String.format("Duplicate event handler %s instance!", handler);
			throw new RuntimeException(error);
		}

		REST_SERVICE_PROCESSORS.add(handler);

	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
