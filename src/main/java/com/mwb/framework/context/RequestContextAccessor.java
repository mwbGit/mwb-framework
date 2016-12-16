package com.mwb.framework.context;

import com.mwb.framework.http.context.HttpRequestContextUtility;


public class RequestContextAccessor {
	
	private static final String DEVICE_HEADER_KEY = "DEVICE_HEADER_KEY";
	
	private static final String REST_REQUEST_KEY = "REST_REQUEST_KEY";
	
	private static final String INTERNAL_REMOTE_HOST_NAME = "HOST_NAME";
	
	private static final String INTERNAL_REMOTE_SERVER_NAME = "SERVER_NAME";

	public static void setDeviceHeader(DeviceHeader header) {
		HttpRequestContextUtility.setAttribute(DEVICE_HEADER_KEY, header);
	}
	
	public static void setRestRequest(Boolean restRequest) {
		HttpRequestContextUtility.setAttribute(REST_REQUEST_KEY, restRequest);
	}
	
	public static void setInternalRemoteHostName(String hostName) {
		HttpRequestContextUtility.setAttribute(INTERNAL_REMOTE_HOST_NAME, hostName);
	}
	
	public static void setInternalRemoteServerName(String serverName) {
		HttpRequestContextUtility.setAttribute(INTERNAL_REMOTE_SERVER_NAME, serverName);
	}
	
	public static DeviceHeader getDeviceHeader() {
		return (DeviceHeader) HttpRequestContextUtility.getAttribute(DEVICE_HEADER_KEY);
	}
	
	public static Boolean isRestRequest() {
		Boolean restRequest = (Boolean) HttpRequestContextUtility.getAttribute(REST_REQUEST_KEY);
		
		return (restRequest == null) ? false : restRequest;
	}
	
	public static String getInternalRemoteHostName() {
		return (String)HttpRequestContextUtility.getAttribute(INTERNAL_REMOTE_HOST_NAME);
	}
	
	public static String getInternalRemoteServerName() {
		return (String)HttpRequestContextUtility.getAttribute(INTERNAL_REMOTE_SERVER_NAME);
	}
}
