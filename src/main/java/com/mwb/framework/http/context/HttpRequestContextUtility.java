package com.mwb.framework.http.context;

import com.mwb.framework.security.servlet.session.SessionReplacedRequestHolder;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestContextUtility {    
	public static Object getAttribute(String key) {
		HttpServletRequest request = SessionReplacedRequestHolder.getRequest();

		return (request == null ? null : request.getAttribute(key));
	}

	public static void setAttribute(String key, Object attribute) {
		HttpServletRequest request = SessionReplacedRequestHolder.getRequest();

		if (request != null) {
			request.setAttribute(key, attribute);
		}
	}

	public static void removeAttribute(String key) {
		HttpServletRequest request = SessionReplacedRequestHolder.getRequest();

		if (request != null) {
			request.removeAttribute(key);
		}
	}
	
	
	public static Object getHeader(String name) {
		HttpServletRequest request = SessionReplacedRequestHolder.getRequest();

		return (request == null ? null : request.getHeader(name));
	}

	public static HttpServletRequest getHttpServletRequest() {
		return SessionReplacedRequestHolder.getRequest();
	}
}
