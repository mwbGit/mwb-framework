package com.mwb.framework.security.servlet.session.store;

import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;

public abstract class AbstractSessionStorage implements InitializingBean {
	private String sessionStorageName;
	
	public abstract Serializable get(String sessionId);
	
	public abstract void put(String sessionId, Serializable session);
	
	public abstract void remove(String sessionId);

	protected String getSessionStorageName() {
		return sessionStorageName;
	}

	public void setSessionStorageName(String sessionStorageName) {
		this.sessionStorageName = sessionStorageName;
	}
}
