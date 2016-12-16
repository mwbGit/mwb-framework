package com.mwb.framework.security.servlet.session.global;

import com.mwb.framework.log.Log;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public class GlobalSession implements HttpSession, Serializable {
	private static final Log LOG = Log.getLog(GlobalSession.class);
	
	private static final long serialVersionUID = 1L;

	private final Map<String, Object> attributes;

	private String id;

	private long creationTime;
	
	private int maxInactiveInterval;
	
	private boolean valid;
	
	public GlobalSession(String id) {
		attributes = new ConcurrentHashMap<String, Object>();
		valid = true;
		
		this.id = id;
		
		creationTime = System.currentTimeMillis();
		maxInactiveInterval = -1;
	}
	
	@Override
	public long getCreationTime() {
		return creationTime;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getLastAccessedTime() {
		return System.currentTimeMillis();
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}

	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public Object getValue(String name) {
		return attributes.get(name);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getAttributeNames() {
		return Collections.enumeration(attributes.keySet());
	}

	@Override
	public String[] getValueNames() {
		Set<String> keySet = attributes.keySet();
		
		return keySet.toArray(new String[keySet.size()]);
	}

	@Override
	public synchronized void setAttribute(String name, Object value) {
		// 避免把无效的session设置到hazelcast中，从而导致内存泄露
		if (valid) {
			attributes.put(name, value);
			
			GlobalSessionManager.updateSession(this);
		}
	}

	@Override
	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	@Override
	public synchronized void removeAttribute(String name) {
		if (valid) {
			attributes.remove(name);
			
			GlobalSessionManager.updateSession(this);
		}
	}

	@Override
	public void removeValue(String name) {
		removeAttribute(name);
	}

	@Override
	public synchronized void invalidate() {
		LOG.debug("Invalid session {}", id);
		
		valid = false;
		
		GlobalSessionManager.invalidateSession(id);
	}

	@Override
	public boolean isNew() {
		return false;
	}
	
}
