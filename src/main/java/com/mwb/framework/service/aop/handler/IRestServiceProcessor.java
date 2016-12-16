package com.mwb.framework.service.aop.handler;

import com.mwb.framework.api.common.service.rs.api.ServiceResponse;
import org.aspectj.lang.ProceedingJoinPoint;

public interface IRestServiceProcessor {
	
	public ServiceResponse process(ProceedingJoinPoint pjp, Object[] args) throws Throwable;
	
	public boolean match();
	//值越大，优先级越高
	public Integer sequence();
}
