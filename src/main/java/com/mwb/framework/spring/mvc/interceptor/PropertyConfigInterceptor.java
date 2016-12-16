package com.mwb.framework.spring.mvc.interceptor;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.Map.Entry;

public class PropertyConfigInterceptor extends HandlerInterceptorAdapter {
  
    private Map<String, Object> allProperites;
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    	if (modelAndView != null && allProperites != null) {
    		
    		modelAndView.addAllObjects(allProperites);
    	}
    }

    public void setConfig(Properties properties) {
    	if (properties != null && properties.size() > 0) {
    		allProperites = new HashMap<String, Object>();
    		
			Set<Entry<Object, Object>> entrySet = properties.entrySet();
			Iterator<Entry<Object, Object>> iterator = entrySet.iterator();
			
			while (iterator.hasNext()) {
				Entry<Object, Object> entry = iterator.next();
				
				String key = (String) entry.getKey();
				Object value = entry.getValue();
				
				if (StringUtils.isNotBlank(key) && value != null) {
					allProperites.put(key, value);
				}
			}
    	}
    }
}
