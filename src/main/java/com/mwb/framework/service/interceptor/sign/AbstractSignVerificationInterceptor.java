package com.mwb.framework.service.interceptor.sign;

import com.mwb.framework.service.interceptor.AbstractBasePhaseInterceptor;
import com.mwb.framework.util.HttpUtility;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.message.Message;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public abstract class AbstractSignVerificationInterceptor extends AbstractBasePhaseInterceptor {
	@Value("${service.internal.sign.secret.key}")
	private String secretKey;
	
	public AbstractSignVerificationInterceptor(String phase) {
		super(phase);
	}
	
	protected boolean isSignRequest(Message message) {
		String requestPath = HttpUtility.getPath((String)message.get(Message.REQUEST_URI));
		
		return requestPath != null 
				&& requestPath.startsWith(SignVerificationConstants.INTERNAL_SERVICE_PREFIX);
	}
	
	protected TreeMap<String, String> getUrlParams(String paramString) {		
		TreeMap<String, String> map = new TreeMap<String, String>();		
		if (!StringUtils.isEmpty(paramString)) {
			List<String> parts = Arrays.asList(paramString.split("&"));
			for (String part : parts) {
				int index = part.indexOf('=');
				String name = null;
				String value = null;
				if (index == -1) {
					name = part;
					value = "";
				} else {
					name = part.substring(0, index);
					value = index < part.length() ? part.substring(index + 1) : "";
				}
				map.put(name, value);
			}
		}
		return map;
	}

	protected String produceSign(TreeMap<String, String> map) {
		StringBuilder sb = new StringBuilder();

		Set<String> keys = map.keySet();
		for (String key : keys) {
			sb.append(key).append("=").append(map.get(key)).append("&");
		}

		String paramString = sb.append("secretKey=").append(secretKey).toString();
		
		return DigestUtils.md5Hex(paramString);
	}
	
	protected String getUrlParamsStr(TreeMap<String, String> map) {
		StringBuilder urlParmsStr = new StringBuilder();
		
		Set<String> keySets = map.keySet();
		for(String key : keySets) {
			urlParmsStr.append("&" + key + "=" + map.get(key));
		}
		String result = urlParmsStr.toString();
		if (result.isEmpty()) {
			return result;
		}
		return result.substring(1);
	}
}
