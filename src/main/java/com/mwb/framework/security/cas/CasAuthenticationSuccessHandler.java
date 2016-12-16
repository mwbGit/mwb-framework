package com.mwb.framework.security.cas;

import com.mwb.framework.security.DefaultUserDetails;
import com.mwb.framework.security.SecurityUtility;
import com.mwb.framework.security.api.ILoginSuccessHandler;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CasAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	private ILoginSuccessHandler loginSuccessHandler;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
		CasAuthenticationToken auth = (CasAuthenticationToken)authentication;
		DefaultUserDetails userDetails = (DefaultUserDetails)auth.getUserDetails();
		
		SecurityUtility.createSession(userDetails);
		
		if (loginSuccessHandler != null) {
			loginSuccessHandler.onSuccess(request, response);
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}

	public void setLoginSuccessHandler(ILoginSuccessHandler loginSuccessHandler) {
		this.loginSuccessHandler = loginSuccessHandler;
	}
}
