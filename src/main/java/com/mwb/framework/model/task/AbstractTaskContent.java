package com.mwb.framework.model.task;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.Serializable;

public abstract class AbstractTaskContent implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
