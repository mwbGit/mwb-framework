package com.mwb.framework.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.Serializable;

public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String code;
    
    public Location() {}
    
    public Location(String code) {
    	this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}