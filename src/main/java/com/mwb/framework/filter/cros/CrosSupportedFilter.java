package com.mwb.framework.filter.cros;

import com.mwb.framework.log.Log;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 判断是否支持跨域的filter
 * 
 * @author liugang
 *
 */
public class CrosSupportedFilter implements Filter {
	private static final Log LOG = Log.getLog(CrosSupportedFilter.class);
	
	private String corsAllowCredentials;
	
	private String corsAllowOrigins;
	
	private Set<String> allowOriginSet = new HashSet<String>();
	
	@Value("${cors.access.control.allow.credentials:true}")
	public void setCorsAllowCredentials(String corsAllowCredentials) {
		this.corsAllowCredentials = corsAllowCredentials;
	}

	@Value("${cors.access.control.allow.origin:}")
	public void setCorsAllowOrigins(String corsAllowOrigins) {
		this.corsAllowOrigins = corsAllowOrigins;
		
		initAllowOriginSet();
	}

	private void initAllowOriginSet() {
		if (corsAllowOrigins != null) {
			String[] origins = StringUtils.split(corsAllowOrigins, ",");
			for (String origin : origins) {
				
				if (StringUtils.startsWith(origin, "*.")) { // 仅支持*。开头的域
					
					int firstDotIndex = StringUtils.indexOf(origin, ".");
					allowOriginSet.add(origin.substring(firstDotIndex + 1).trim());
				}
			}
		}
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOG.debug(CrosSupportedFilter.class + " initilized.");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		
		String origin = httpRequest.getHeader("Origin");
		if (StringUtils.isNotBlank(origin)) {
			
			boolean isAllow = false;
			
			for (String allowOrigin : allowOriginSet) {
				if (origin.trim().endsWith(allowOrigin)) {
					isAllow = true;
					break;
				}
			}
			
			if (isAllow) {
				httpResponse.setHeader("Access-Control-Allow-Origin", origin);
				httpResponse.setHeader("Access-Control-Allow-Credentials", corsAllowCredentials);
				
				if (httpRequest.getMethod().equals("OPTIONS")) {
					httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS"); 
					httpResponse.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, shbj-device, shbj-export");
					
					httpResponse.sendError(HttpServletResponse.SC_OK); // 直接结束请求
					return;
				}
			} else {
				
				httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Do not allow cross domain access!");
				
				LOG.warn("Origin=[{}] don't allow cross domain access content.", origin);
				return;
			}		
		}
		
		chain.doFilter(httpRequest, httpResponse);

	}

	@Override
	public void destroy() {
		LOG.debug(CrosSupportedFilter.class + " destroied.");
	}
}
