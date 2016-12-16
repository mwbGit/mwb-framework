package com.mwb.framework.context.model;

import com.mwb.framework.model.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AccountCO implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String CONTEXT_KEY = "USER_DETAIL_CONTEXT_KEY";

	private String code;
	private String name;
	
	private List<Location> locations;
	private Set<String> permissions;

	public static String getContextKey() {
		return CONTEXT_KEY;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}

	public List<String> getLocationCodes() {
		if (locations != null && locations.size() > 0) {
			List<String> codes = new ArrayList<String>();

			for (Location location : locations) {
				codes.add(location.getCode());
			}

			return codes;
		}

		return null;
	}
}
