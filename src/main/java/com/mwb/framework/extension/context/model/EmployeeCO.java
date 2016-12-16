package com.mwb.framework.extension.context.model;

import com.mwb.framework.context.model.AccountCO;

public class EmployeeCO extends AccountCO {
	private static final long serialVersionUID = 1L;

	private String employeePositionCode;
	private String deliveryStaffCode;
	
	private String positionCode;
	private String positionName;

	public EmployeeCO() {
		
	}
	
	public EmployeeCO(AccountCO account) {
		this.setCode(account.getCode());
		this.setName(account.getName());
		this.setPermissions(account.getPermissions());
		this.setLocations(account.getLocations());
		
	}

	public String getDeliveryStaffCode() {
		return deliveryStaffCode;
	}

	public void setDeliveryStaffCode(String deliveryStaffCode) {
		this.deliveryStaffCode = deliveryStaffCode;
	}

	public String getEmployeePositionCode() {
		return employeePositionCode;
	}

	public void setEmployeePositionCode(String employeePositionCode) {
		this.employeePositionCode = employeePositionCode;
	}

	public String getPositionCode() {
		return positionCode;
	}

	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
}
