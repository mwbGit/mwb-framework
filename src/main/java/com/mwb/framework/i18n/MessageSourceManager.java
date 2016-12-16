package com.mwb.framework.i18n;

import com.mwb.framework.bean.context.BeanContextUtility;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.AbstractMessageSource;

public class MessageSourceManager {
    private static AbstractMessageSource messageSource;

    private MessageSourceManager() {

    }

    public static String getMessage(String code) {
        return getMessageSource().getMessage(code, null, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String code, Object[] args) {
        return getMessageSource().getMessage(code, args, LocaleContextHolder.getLocale());
    }

    private static AbstractMessageSource getMessageSource() {
    	if (messageSource == null) {
    		messageSource = BeanContextUtility.getBean(AbstractMessageSource.class);
    	}
    	return messageSource;
    }

}
