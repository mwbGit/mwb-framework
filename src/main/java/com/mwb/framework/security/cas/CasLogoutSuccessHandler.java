package com.mwb.framework.security.cas;

import com.mwb.framework.security.api.ILogoutSuccessHandler;
import com.mwb.framework.util.HttpUtility;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CasLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
	
	private ILogoutSuccessHandler logoutSuccessHandler;
	
	private CasServiceProperties casServiceProperties;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		if (logoutSuccessHandler != null) {
			logoutSuccessHandler.onSuccess(request, response);
		}
    	
    	super.onLogoutSuccess(request, response, authentication);
	}
	
	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
		return casServiceProperties.getCasLogoutUrl() + "?service="	
					+ HttpUtility.getServerBaseUrl(request.getRequestURL().toString());
    }

	public void setLogoutSuccessHandler(ILogoutSuccessHandler logoutSuccessHandler) {
		this.logoutSuccessHandler = logoutSuccessHandler;
	}
	
	public void setServiceProperties(CasServiceProperties serviceProperties) {
		this.casServiceProperties = serviceProperties;
	}
}
