package com.mwb.framework.context;

import com.mwb.framework.context.model.AccountCO;
import com.mwb.framework.http.context.HttpSessionContextUtility;

import java.util.Set;


public class SessionContextAccessor {

	@SuppressWarnings("unchecked")
	public static <T extends AccountCO> T getCurrentAccount() {
		return (T) HttpSessionContextUtility.getAttribute(AccountCO.getContextKey());
    }
	
	public static Set<String> getPermissions() {
		AccountCO account = getCurrentAccount();
		
		if (account != null) {
			return account.getPermissions();
		} else {
			return null;
		}
    }
	
}