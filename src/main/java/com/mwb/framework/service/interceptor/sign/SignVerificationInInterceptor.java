package com.mwb.framework.service.interceptor.sign;

import com.mwb.framework.log.Log;
import com.mwb.framework.service.interceptor.exception.SignVerificationException;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import java.util.TreeMap;

public class SignVerificationInInterceptor extends AbstractSignVerificationInterceptor {
	private static final Log LOG = Log.getLog(SignVerificationInInterceptor.class);
	
	public SignVerificationInInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	@Override
	public void handleMessage(Message message) {
		if (isRequestor(message)) {
			return;
		}
		
		if (!isSignRequest(message)) {
			return;
		}
		
		try {
			TreeMap<String, String> map = getUrlParams((String)message.get(Message.QUERY_STRING));
			
			String sign = map.get(SignVerificationConstants.PARAM_SIGN);
			if (StringUtils.isBlank(sign)) {
				throw new SignVerificationException();
			}
			
			map.remove(SignVerificationConstants.PARAM_SIGN);
	
			// 校验密码
			if (!checkSign(map, sign)) {
				throw new SignVerificationException();
			}
		} catch (Exception e) {
			LOG.error("Catch an exception", e);
			
			handleException(message, e);
		}
	}
	
	private boolean checkSign(TreeMap<String, String> map, String sign) {
		return StringUtils.equals(produceSign(map), sign);
	}
}
