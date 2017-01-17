package com.mwb.framework.api.jms;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String date;
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
