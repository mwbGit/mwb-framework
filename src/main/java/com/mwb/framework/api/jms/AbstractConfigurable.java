package com.mwb.framework.api.jms;

import org.apache.commons.lang.StringUtils;

public abstract class AbstractConfigurable implements IConfigurable {
	
	public static final String CONCURRENCY = "5-20";
	
	@Override
	public String getConcurrency() {
		return CONCURRENCY;
	}
	
	@Override
	public String getTopicConsumerName() {
		return StringUtils.replaceChars(this.getClass().getName(), '.', '/');
	}
}
