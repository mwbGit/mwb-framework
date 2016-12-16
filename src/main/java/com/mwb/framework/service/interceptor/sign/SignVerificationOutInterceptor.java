package com.mwb.framework.service.interceptor.sign;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import java.util.TreeMap;

public class SignVerificationOutInterceptor extends AbstractSignVerificationInterceptor {
	
	public SignVerificationOutInterceptor() {
		super(Phase.PREPARE_SEND);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		if (!isRequestor(message)) {
			return;
		}
		
		if (!isSignRequest(message)) {
			return;
		}
		
		String signedUri = getSignedUri((String)message.get(Message.REQUEST_URI));
		
		message.put(Message.REQUEST_URI, signedUri);
		message.put(Message.ENDPOINT_ADDRESS, signedUri);		
	}
	
	private String getSignedUri(String requestUri) {
		String queryString = "";
		String pathInfo = requestUri;
		
		int index = requestUri.indexOf('?');
		if (index >= 0) {
			pathInfo = requestUri.substring(0, index);
			queryString = requestUri.substring(index + 1);
		}
		
		TreeMap<String, String> map = getUrlParams(queryString);
		
		Long time = System.currentTimeMillis() / 1000;
		map.put(SignVerificationConstants.PARAM_TIMESTAMP, time.toString());
		
		String sign = produceSign(map);
		map.put(SignVerificationConstants.PARAM_SIGN, sign);
		
		requestUri = pathInfo + "?" + getUrlParamsStr(map);
		
		return requestUri;
	}
}
