package com.mwb.framework.context;

import com.mwb.framework.context.model.AccountCO;

import java.util.List;

public class AuthorizedLocationAccessor {
	
	public static String[] getLocations() {
		AccountCO account = null;
		
		try {
			account = SessionContextAccessor.getCurrentAccount();
		} catch (IllegalStateException e) {			
		}
		
		if (account != null) {
			List<String> locations = account.getLocationCodes();
			
			if (locations != null && locations.size() > 0) {
				return locations.toArray(new String[locations.size()]);
			} else {
				return new String[] {"N"};
			}
		} else {
			return null;
		}
	}
	
	public static String[] getQueryLocations() {
		String[] locations = getLocations();
		
		if (locations != null) {
			for (int i = 0; i < locations.length; i ++) {
				locations[i] = locations[i] + "%";
			}
		}
		
		return locations;
	}
	
}
