package com.mwb.framework.security.servlet.session.global;

import com.mwb.framework.log.Log;
import com.mwb.framework.security.servlet.session.ISessionManager;
import com.mwb.framework.security.servlet.session.store.AbstractSessionStorage;
import com.mwb.framework.util.CookieUtility;
import com.mwb.framework.util.RandomGenerator;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class GlobalSessionManager implements ISessionManager {
	private static final Log LOG = Log.getLog(GlobalSessionManager.class);
	
	private static final String SESSION_COOKIE_NAME = "GSESSIONID";
	
	private static AbstractSessionStorage sessionStorage;
	
	private final ThreadLocal<String> sessionIds;
	
	private final ThreadLocal<HttpServletResponse> responses;
	
	
	public GlobalSessionManager() {
		sessionIds = new ThreadLocal<String>();
		responses = new ThreadLocal<HttpServletResponse>();
	}
	
	@Override
	public void initialize(HttpServletRequest request, HttpServletResponse response) {
		// If cannot get session id from request parameter, try to get session id from cookie, usually for browser request
		String sessionId = CookieUtility.getRawCookie(request, SESSION_COOKIE_NAME);

		if (StringUtils.isNotBlank(sessionId)) {
			LOG.debug("Get GSESSIONID from cookie {}", sessionId);
			
			sessionIds.set(sessionId);
		}
		
		responses.set(response);
	}
	
	public void clear() {
		sessionIds.remove();
		responses.remove();
	}
	
	@Override
	public synchronized HttpSession getSession(boolean create) {
		String sessionId = sessionIds.get();
		
		if (create) {	
			if (StringUtils.isBlank(sessionId)) {
				return createSession(RandomGenerator.getGlobalUniqueId());
			} else {
				GlobalSession session = (GlobalSession)sessionStorage.get(sessionId);
				
				if (session == null) {
					return createSession(sessionId);
				} else {
					return session;
				}
			}
		} else {
			if (StringUtils.isBlank(sessionId)) {
				return null;
			} else {
				return (GlobalSession)sessionStorage.get(sessionId);
			}
		}
	}
	
	private GlobalSession createSession(String sessionId) {
		LOG.info("Create a new session {}", sessionId);
		
		GlobalSession session = new GlobalSession(sessionId);
		
		sessionStorage.put(sessionId, session);
		
		sessionIds.set(sessionId);
		
		HttpServletResponse response = responses.get();
		
		if (response != null) {
			
			Cookie cookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
			cookie.setMaxAge(-1);
			cookie.setPath("/");
			
			response.addCookie(cookie);
		}
		
		return session;
	}
	
	public static synchronized void invalidateSession(String sessionId) {
		if (sessionId != null) {
			sessionStorage.remove(sessionId);
		}
	}
	
	protected static synchronized void updateSession(GlobalSession session) {
		sessionStorage.put(session.getId(), session);
	}

	public void setSessionStorage(AbstractSessionStorage sessionStorage) {
		GlobalSessionManager.sessionStorage = sessionStorage;
	}

}
