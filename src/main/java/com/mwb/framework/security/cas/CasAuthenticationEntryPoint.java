package com.mwb.framework.security.cas;

import com.alibaba.fastjson.JSON;
import com.mwb.framework.api.common.http.ContentType;
import com.mwb.framework.api.common.service.rs.api.ServiceResponse;
import com.mwb.framework.context.RequestContextAccessor;
import com.mwb.framework.log.Log;
import com.mwb.framework.util.HttpUtility;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CasAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {

	private static final String NO_AUTHENTICATION_RESPONSE_CODE = "900101";
	
	private final static Log LOG = Log.getLog(CasAuthenticationEntryPoint.class);

	private CasServiceProperties casServiceProperties;

	public final void commence(final HttpServletRequest servletRequest, final HttpServletResponse response,
			final AuthenticationException authenticationException) throws IOException, ServletException {
		
		if (RequestContextAccessor.isRestRequest()) {
			
			response.addHeader("Content-Type", ContentType.APPLICATION_JSON_UTF8);
			
			ServiceResponse serviceResponse  = new ServiceResponse();
			serviceResponse.setResultCode(NO_AUTHENTICATION_RESPONSE_CODE);
			serviceResponse.setResultMessage("NO_AUTHENTICATION");
			
			LOG.debug("Mobile APP commence RequestURI:{}.", servletRequest.getRequestURI());
			response.getOutputStream().print(JSON.toJSONString(serviceResponse));
			
		} else {
			
			LOG.debug("To commence user-agent:{}.", servletRequest.getHeader("user-agent"));
			
			final String urlEncodedService = createServiceUrl(servletRequest, response);
			final String redirectUrl = createRedirectUrl(servletRequest, urlEncodedService);

			LOG.debug("PC web commence RequestURI:{}, redirectUrl:{}.", servletRequest.getRequestURI(), redirectUrl);
			response.sendRedirect(redirectUrl);
		}
	}
	
	public String createServiceUrl(final HttpServletRequest request, final HttpServletResponse response) {
		return CommonUtils.constructServiceUrl(null, response, this.casServiceProperties.getService(), null, this.casServiceProperties.getArtifactParameter(), true);
	}

	public final void setCasServiceProperties(CasServiceProperties serviceProperties) {
		this.casServiceProperties = serviceProperties;
	}
	
	/**
	 * 获取完整的CAS登录地址
	 * 
	 * @param serviceUrl service url
	 * @return
	 */
	public String createRedirectUrl(HttpServletRequest request, String serviceUrl) {
		StringBuffer sb = new StringBuffer();

		sb.append(HttpUtility.getServerBaseUrl(request.getRequestURL().toString()))
			.append(casServiceProperties.getFilterProcessesUrl());

		String encodedOtherParameterValue = null;
		try {
			encodedOtherParameterValue = URLEncoder.encode(sb.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("Cas login redirect url encode failed", e);
		}

		String redirectUrl = 
				CommonUtils.constructRedirectUrl(casServiceProperties.getCasLoginUrl(), 
						this.casServiceProperties.getServiceParameter(), serviceUrl, this.casServiceProperties.isSendRenew(), false);
		StringBuffer redirectUrlsb = new StringBuffer();
		redirectUrlsb.append(redirectUrl).append("&").append("targetUrl").append("=").append(encodedOtherParameterValue);

		redirectUrl = redirectUrlsb.toString();

		return redirectUrl;
	}
	
	public String getService() {
        return this.casServiceProperties.getService();
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.casServiceProperties, "serviceProperties must be specified");
		Assert.notNull(this.casServiceProperties.getService(),"serviceProperties.getService() cannot be null.");
	}

}
