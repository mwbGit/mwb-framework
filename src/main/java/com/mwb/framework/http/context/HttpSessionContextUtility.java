package com.mwb.framework.http.context;

import com.mwb.framework.security.servlet.session.SessionReplacedRequestHolder;

import javax.servlet.http.HttpSession;

public class HttpSessionContextUtility {    
    public static Object getAttribute(String key) {
    	HttpSession session = SessionReplacedRequestHolder.getSession();
    	
    	return (session == null ? null : session.getAttribute(key));
    }

    public static void setAttribute(String key, Object attribute) {
    	HttpSession session = SessionReplacedRequestHolder.getSession();
    	
    	if (session != null) {
    		session.setAttribute(key, attribute);
    	}
    }
    
    public static void removeAttribute(String key) {
    	HttpSession session = SessionReplacedRequestHolder.getSession();
    	
    	if (session != null) {
    		session.removeAttribute(key);
    	}
    }
    
    public static HttpSession getSession() {
    	return SessionReplacedRequestHolder.getSession();
    }
}
