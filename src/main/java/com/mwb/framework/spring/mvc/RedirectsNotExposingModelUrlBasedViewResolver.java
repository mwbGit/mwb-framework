package com.mwb.framework.spring.mvc;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.util.Locale;

// This class is to fix the default view resolver append request parameters in URL issue
public class RedirectsNotExposingModelUrlBasedViewResolver extends UrlBasedViewResolver {
	
	@Override
    protected View createView(String viewName, Locale locale) throws Exception {
        View view = super.createView(viewName, locale);
        
        if (view instanceof RedirectView) {
            ((RedirectView) view).setExposeModelAttributes(false);
        }
        
        return view;
    }
}
