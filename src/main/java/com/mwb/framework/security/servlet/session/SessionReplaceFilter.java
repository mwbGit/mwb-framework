package com.mwb.framework.security.servlet.session;

import com.mwb.framework.context.DeviceHeader;
import com.mwb.framework.context.RequestContextAccessor;
import com.mwb.framework.log.Log;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/*
 * REST request is defined as AJAX request from browser or HTTP request from Android/IOS application
 * SHBJ applications use "shbj-source" HTTP header to mark a REST request
 */
public class SessionReplaceFilter extends GenericFilterBean {

	private static final Log LOG= Log.getLog(SessionReplaceFilter.class);
	
	private static final String HEADER_NAME_DEVICE = "shbj-device";
	
	private static final String PARAM_NAME_JSONP = "callback";
	
	private ISessionManager globalSessionManager;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		try {
			HttpServletRequest req = (HttpServletRequest)request;
			HttpServletResponse resp = (HttpServletResponse)response;
			
			DeviceHeader deviceHeader = parseDeviceHeader(req);
			
			String jsonpParam = req.getParameter(PARAM_NAME_JSONP);
			
			Boolean restRequest = deviceHeader != null || StringUtils.isNotBlank(jsonpParam);
			
			globalSessionManager.initialize(req, resp);
			
			SessionReplacedRequest replaceRequest = new SessionReplacedRequest(req, globalSessionManager);
			
			SessionReplacedRequestHolder.initialize(replaceRequest);
			
			RequestContextAccessor.setRestRequest(restRequest);
			RequestContextAccessor.setDeviceHeader(deviceHeader);
			
			chain.doFilter(replaceRequest, response);
			
		} catch (Exception e) {
			LOG.error("Catch an exception!", e);
		} finally {
			clear();
		}
	}
	
	private DeviceHeader parseDeviceHeader(HttpServletRequest request) {
		LOG.debug("Referer:{}, RequestURL:{}.", 
				request.getHeader("Referer"), request.getRequestURL());
		
		Object deviceObj = request.getHeader(HEADER_NAME_DEVICE);
		
		if (deviceObj == null) {
			LOG.debug("The {} header is not set!", HEADER_NAME_DEVICE);
			return null;
		}
		
		String value = null;
		if (deviceObj instanceof List) {
			Object obj = ((List<?>)deviceObj).get(0);
			// 是否为String
			if (obj instanceof String) {
				deviceObj = (String)obj;
			}
		} else if (deviceObj instanceof String) {
			value = (String) deviceObj;
		}
		
		if (value != null) {
			LOG.debug("Header {}={}", HEADER_NAME_DEVICE, value);
			
			DeviceHeader header = DeviceHeader.fromCode(value);
			
			return header;
		}
		
		return null;
	}
	
	private void clear() {
		globalSessionManager.clear();
		
		SessionReplacedRequestHolder.clear();
	}

	public void setGlobalSessionManager(ISessionManager globalSessionManager) {
		this.globalSessionManager = globalSessionManager;
	}
}