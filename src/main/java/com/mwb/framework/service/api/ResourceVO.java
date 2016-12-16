package com.mwb.framework.service.api;

import com.mwb.framework.util.Constants;

public class ResourceVO {
	
	private final static ResourceVO noneResource;
	
	private String label;
	private Object value;
	
	static {
		noneResource = new ResourceVO();
		
		noneResource.setLabel("");
		noneResource.setValue(Constants.NONE_SELECT_CODE);
	}
	
	public static ResourceVO getNoneResource() {
		return noneResource;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
}
