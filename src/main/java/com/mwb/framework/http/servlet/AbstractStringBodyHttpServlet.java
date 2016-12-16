package com.mwb.framework.http.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class AbstractStringBodyHttpServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	protected static final String CHARACTER_ENCODING = "UTF-8";
	
	protected String getBodyAsString(HttpServletRequest request) throws Exception {
		if (request.getMethod().equals("POST")) {
			String body = null;
		    StringBuilder stringBuilder = new StringBuilder();
		    BufferedReader bufferedReader = null;

		    try {
		        InputStream inputStream = request.getInputStream();
		        if (inputStream != null) {
		            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		            char[] charBuffer = new char[128];
		            int bytesRead = -1;
		            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
		                stringBuilder.append(charBuffer, 0, bytesRead);
		            }
		        } else {
		            return null;
		        }
		    } catch (IOException e) {
		    	throw new Exception("Failed to read body from request.", e);
		    } finally {
		        if (bufferedReader != null) {
		            try {
		                bufferedReader.close();
		            } catch (IOException e) {
		            	throw new Exception("Failed to cloase BufferedReader.", e);
		            }
		        }
		    }

		    body = stringBuilder.toString();
		    return body;
		}
		
		return null;
	}
	
}
