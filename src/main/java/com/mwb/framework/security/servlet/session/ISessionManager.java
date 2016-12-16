package com.mwb.framework.security.servlet.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface ISessionManager {
	public HttpSession getSession(boolean create);
	
	public void initialize(HttpServletRequest request, HttpServletResponse response);
	
	public void clear();
}
