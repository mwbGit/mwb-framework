package com.mwb.framework.context;

import java.util.HashMap;
import java.util.Map;

public enum DeviceHeader {
	ANDROID("ANDROID"),
	IOS("IOS"),
	BROWSER("BROWSER");
	
	private static final Map<String, DeviceHeader> code2DeviceHeader;
	
	private String code;
	
	static {
		code2DeviceHeader = new HashMap<String, DeviceHeader>();

        for (DeviceHeader deviceHeader : DeviceHeader.values()) {
        	code2DeviceHeader.put(deviceHeader.getCode(), deviceHeader);
        }
    }
	
	public static DeviceHeader fromCode(String code) {
        return code2DeviceHeader.get(code);
    }

	DeviceHeader(String code) {
        this.code = code;
    }
	
	public String getCode() {
        return code;
    }
}
