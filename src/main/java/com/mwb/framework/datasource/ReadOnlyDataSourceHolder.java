package com.mwb.framework.datasource;


public class ReadOnlyDataSourceHolder {

	private static final ThreadLocal<Boolean> readOnly;
	
	static {
		readOnly = new ThreadLocal<Boolean>();
	}
	
	public static boolean isReadOnly() {
		Boolean b = readOnly.get();
		return b != null && b;
	}
	
	public static void setReadOnly() {
		readOnly.set(true);
	}
	
	public static void clear() {
		readOnly.remove();
	}
}
