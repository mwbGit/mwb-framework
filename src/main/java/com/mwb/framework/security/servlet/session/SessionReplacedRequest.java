package com.mwb.framework.security.servlet.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

public class SessionReplacedRequest extends HttpServletRequestWrapper {

	private ISessionManager sessionManager;
	
	public SessionReplacedRequest(HttpServletRequest request, ISessionManager sessionManager) {
		super(request);
		
		this.sessionManager = sessionManager;
	}

	@Override
	public HttpSession getSession(boolean create) {
		return sessionManager.getSession(create); 
	}

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}
	
}
