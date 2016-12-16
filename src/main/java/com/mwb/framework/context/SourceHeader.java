package com.mwb.framework.context;

import java.util.HashMap;
import java.util.Map;

public enum SourceHeader {
	ANDROID("ANDROID"),
	IOS("IOS"),
	BROWSER("BROWSER");
	
	private static final Map<String, SourceHeader> code2SourceHeader;
	
	private String code;
	
	static {
		code2SourceHeader = new HashMap<String, SourceHeader>();

        for (SourceHeader sourceHeader : SourceHeader.values()) {
        	code2SourceHeader.put(sourceHeader.getCode(), sourceHeader);
        }
    }
	
	public static SourceHeader fromCode(String code) {
        return code2SourceHeader.get(code);
    }

	SourceHeader(String code) {
        this.code = code;
    }
	
	public String getCode() {
        return code;
    }
}
