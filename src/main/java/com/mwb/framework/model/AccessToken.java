package com.mwb.framework.model;

import java.io.Serializable;

public class AccessToken implements Serializable {
	private static final long serialVersionUID = 7402852210232575652L;

	private final String token;
	private final long expireInMilliSecond;
	
	public AccessToken(String token, long expireInMilliSecond) {
		this.token = token;
		this.expireInMilliSecond = expireInMilliSecond;
	}

	public String getToken() {
		return token;
	}

	public long getExpireInMilliSecond() {
		return expireInMilliSecond;
	}
}
