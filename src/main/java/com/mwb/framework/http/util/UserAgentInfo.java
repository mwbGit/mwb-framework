package com.mwb.framework.http.util;

public class UserAgentInfo {
	private String browserType;
	private String browserVersion;
	private String userAgent;
	
	public String getBrowserType() {
		return browserType;
	}
	
	public void setBrowserType(String browserType) {
		this.browserType = browserType;
	}
	
	public String getBrowserVersion() {
		return browserVersion;
	}
	
	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}
	
	public String getUserAgent() {
		return userAgent;
	}
	
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
}
