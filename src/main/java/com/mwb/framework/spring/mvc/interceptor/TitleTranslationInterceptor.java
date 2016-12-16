package com.mwb.framework.spring.mvc.interceptor;

import com.mwb.framework.i18n.MessageSourceManager;
import com.mwb.framework.log.Log;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TitleTranslationInterceptor extends HandlerInterceptorAdapter {
    private Log LOG = Log.getLog(TitleTranslationInterceptor.class);

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    	
    	if (modelAndView != null) {
	        ModelMap modelMap = modelAndView.getModelMap();
	        String titleKey = (String)modelMap.remove("titleKey");
	
	        if (StringUtils.isNotBlank(titleKey)) {
	            try {
	                String title = MessageSourceManager.getMessage(titleKey);
	
	                modelAndView.addObject("title", title);
	            } catch (Exception e) {
	                LOG.error("Catch an Exception!", e);
	            }
	        }
    	}
    }

}
