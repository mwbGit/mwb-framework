package com.mwb.framework.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class RoutingDataSource extends AbstractRoutingDataSource {

	private static final String READ_ONLY_DATA_SOURCE_KEY = "READ_ONLY";
	
	@Override
	protected Object determineCurrentLookupKey() {
		if (ReadOnlyDataSourceHolder.isReadOnly()) {
			return READ_ONLY_DATA_SOURCE_KEY;
		}
		return null;
	}
	
	public void setReadOnlyDataSource(DataSource readOnlyDataSource) {
		Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
		if (readOnlyDataSource != null) {
			targetDataSources.put(READ_ONLY_DATA_SOURCE_KEY, readOnlyDataSource);
		}
		super.setTargetDataSources(targetDataSources);		
	}
}
