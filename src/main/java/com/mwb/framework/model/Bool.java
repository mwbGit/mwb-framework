package com.mwb.framework.model;

import com.mwb.framework.i18n.EnumMessageTranslator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public enum Bool implements INameInterface {
    Y(true, "Y", "Yes"), 
    N(false, "N", "No");

    private static final Map<Boolean, Bool> value2Bool;
    private static final Map<String, Bool> code2Bool;
    private static Map<String, Bool> name2Bool;

    private boolean value;
    private String code;
    private String description;

    static {
        value2Bool = new HashMap<Boolean, Bool>();
        code2Bool = new HashMap<String, Bool>();
        
        for (Bool bool : Bool.values()) {
            value2Bool.put(bool.getValue(), bool);
            code2Bool.put(bool.getCode(), bool);
        }
    }

    public static Bool fromValue(Boolean value) {
    	if (value == null) {
    		return null;
    	}
    	
        return value2Bool.get(value);
    }

    public static Bool fromCode(String code) {
    	if (code == null) {
    		return null;
    	} else {
            code = code.toUpperCase();
        }
        
        return code2Bool.get(code);
    }
    
    public static Bool fromName(String name) {
    	if(name2Bool == null){
    		name2Bool = new ConcurrentHashMap<String, Bool>();
    		for (Bool bool : Bool.values()) {               
                name2Bool.put(bool.getName(), bool);
            }
    	}
        return name2Bool.get(name);
    }

    Bool(boolean value, String code, String description) {
        this.value = value;
        this.code = code;
        this.description = description;
    }

    public boolean getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return EnumMessageTranslator.getName(getClass().getSimpleName() + "." + code);
    }
}

