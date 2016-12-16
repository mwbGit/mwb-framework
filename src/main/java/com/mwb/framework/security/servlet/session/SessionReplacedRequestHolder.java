package com.mwb.framework.security.servlet.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionReplacedRequestHolder {
	private static final ThreadLocal<HttpServletRequest> requests;
	
	static {
		requests = new ThreadLocal<HttpServletRequest>();
	}
	
	public static void initialize(HttpServletRequest request) {
		requests.set(request);
	}
	
	public static void clear() {
		requests.remove();
	}
	
	public static HttpServletRequest getRequest() {
		return requests.get();
	}
    
    public static HttpSession getSession() {
    	HttpServletRequest request = getRequest();
    	
    	return (request == null ? null : request.getSession());
    }
}
