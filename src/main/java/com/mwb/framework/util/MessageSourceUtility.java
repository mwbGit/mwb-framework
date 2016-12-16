package com.mwb.framework.util;

import com.mwb.framework.i18n.MessageSourceManager;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

public class MessageSourceUtility {

    public static String getNewInternalNote(String newNote, 
    		String employeeName, String orderStatusName) {
    	return getNewInternalNote(null, newNote, employeeName, orderStatusName);
    }
    
    public static String getNewInternalNote(String oldNote, 
    		String newNote, String employeeName, String orderStatusName) {
    	if (StringUtils.isBlank(newNote)) {
    		return oldNote;
    	}
    	
    	newNote = MessageSourceManager.getMessage("content.order.postpone.check", 
         		new String[]{employeeName, DateTimeUtility.formatYYYYMMDDHHMM(new Date()), orderStatusName, newNote});
    	
    	return StringUtils.isBlank(oldNote) ? newNote : oldNote + "<br> " + newNote; 
    }

    public static String getNewTimeoutNote(String oldNote, String newNote, String employeeName) {
    	if (StringUtils.isBlank(newNote)) {
    		return oldNote;
    	}
    	
    	newNote = MessageSourceManager.getMessage("content.order.timeout.check", 
         		new String[]{employeeName, DateTimeUtility.formatYYYYMMDDHHMM(new Date()), newNote});
    	
    	return StringUtils.isBlank(oldNote) ? newNote : oldNote + "<br> " + newNote; 
    }
}
