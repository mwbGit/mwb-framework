package com.mwb.framework.security;

import com.mwb.framework.context.model.AccountCO;
import com.mwb.framework.http.context.HttpSessionContextUtility;

public class SecurityUtility {

	public static void createSession(DefaultUserDetails userDetails) {
		HttpSessionContextUtility.setAttribute(AccountCO.getContextKey(), userDetails.getAccount());		
	}
}
