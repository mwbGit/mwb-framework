package com.mwb.framework.mybatis;

import org.mybatis.spring.mapper.MapperScannerConfigurer;

import java.util.Properties;

public class BasePackageRetrievableMapperScannerConfigurer extends MapperScannerConfigurer {
	private static final String BASE_PACKAGE = "mybatis.mapper.base.package";
	
	private String basePackage;
	
	public String getBasePackage() {
		return basePackage;
	}

	public void setConfig(Properties properties) {
		basePackage = properties.getProperty(BASE_PACKAGE);
		
		super.setBasePackage(basePackage);
	}
}
