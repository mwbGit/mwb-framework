package com.mwb.framework.cache;

import com.mwb.framework.log.Log;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CacheFilter implements Filter, BeanPostProcessor {
	private static final Log LOG = Log.getLog(CacheFilter.class);
	
	private List<ICache> cacheHolders;
	
	public CacheFilter() {
		cacheHolders = new ArrayList<ICache>();
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOG.debug(getClass() + " initilized.");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		flushCache();
		
		response.getOutputStream().print("OK");
		
		return;
	}
	
	private void flushCache() {
		long startTime = System.currentTimeMillis();
		
		for (ICache cacheHolder : cacheHolders) {
			cacheHolder.clearAll();
		}
		
		long endTime = System.currentTimeMillis();
		
		LOG.info("Cache is flushed in {} ms", endTime - startTime);
	}

	@Override
	public void destroy() {
		LOG.debug(getClass() + " destoried.");
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof ICache) {
			cacheHolders.add((ICache)bean);
		}
		
		return bean;
	}

}
