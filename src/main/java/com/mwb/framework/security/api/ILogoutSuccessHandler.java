package com.mwb.framework.security.api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ILogoutSuccessHandler {

	public void onSuccess(HttpServletRequest request,
						  HttpServletResponse response)
			throws IOException, ServletException;
}
