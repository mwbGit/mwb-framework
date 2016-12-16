package com.mwb.framework.extension.security;

import com.mwb.framework.extension.context.model.DeliveronlyStoreAccountCO;
import com.mwb.framework.extension.context.model.EmployeeCO;
import com.mwb.framework.log.Log;
import com.mwb.framework.model.Location;
import com.mwb.framework.security.DefaultUserDetails;
import com.mwb.framework.security.IUserDetailsBuilder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.Map.Entry;

public class DefaultUserDetailsBuilder implements IUserDetailsBuilder {
	
	private static final Log LOG = Log.getLog(DefaultUserDetailsBuilder.class);
	
	private enum AccountType {
		EMPLOYEE			("EMPLOYEE"),
		DELIVERONLY_STORE	("DELIVERONLY_STORE");

		protected String code;
		
		private  AccountType(String code) {
			this.code = code;
		}
	}
	
	@Override
	public UserDetails build(Map<String, Object> attributes) {
		Set<Entry<String, Object>> entries = attributes.entrySet();
		for (Entry<String, Object> entry : entries) {
			LOG.debug("key {}, value {}", entry.getKey(), entry.getValue());
		}
		
		final String accountType = (String) attributes.get("ACCOUNT_TYPE");
		final String code = (String) attributes.get("CODE");
		final String mobile = (String) attributes.get("MOBILE");
		final String fullName = (String) attributes.get("FULL_NAME");
		
		String locationsString = (String) attributes.get("LOCATIONS");
		List<String> locationCodes = new ArrayList<String>(Arrays.asList(locationsString.replace("[", "").replace("]", "").replace(" ","").split(",")));
		List<Location> locations = new ArrayList<Location>();
		
		for (String locationCode : locationCodes) {
			Location location = new Location(locationCode);
			locations.add(location);
		}
		
		String permissionsString = (String) attributes.get("PERMISSIONS");
		List<String> permissions = new ArrayList<String>(Arrays.asList(permissionsString.replace("[", "").replace("]", "").replace(" ","").split(",")));
		
		if (AccountType.EMPLOYEE.code.equals(accountType)) {
			final String deliveryStaffCode = (String) attributes.get("DELIVERY_STAFF_CODE");
			final String employeePositionCode = (String) attributes.get("EMPLOYEE_POSITION_CODE");
			final String positionCode = (String) attributes.get("POSITION_CODE");
			final String positionName = (String) attributes.get("POSITION_NAME");
			
			EmployeeCO account = new EmployeeCO();

			account.setCode(code);
			account.setName(fullName);
			account.setLocations(locations);
			account.setPermissions(new HashSet<String>(permissions));
			
			account.setDeliveryStaffCode(deliveryStaffCode);
			account.setEmployeePositionCode(employeePositionCode);
			account.setPositionCode(positionCode);
			account.setPositionName(positionName);
			
			return new DefaultUserDetails(account);
		} else if (AccountType.DELIVERONLY_STORE.code.equals(accountType)) {
			DeliveronlyStoreAccountCO account = new DeliveronlyStoreAccountCO();
			
			account.setCode(code);
			account.setName(fullName);
			account.setMobile(mobile);
			account.setLocations(locations);
			account.setPermissions(new HashSet<String>(permissions));
			
			return new DefaultUserDetails(account);
		}
		
		return null;
	}

}
