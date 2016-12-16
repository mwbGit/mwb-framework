package com.mwb.framework.hazelcast;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;

import java.io.IOException;

public class ClasspathXmlClientConfigFactory extends ClientConfig {
	
	public static ClientConfig newConfig(String resource) throws IOException {
		XmlClientConfigBuilder builder = new XmlClientConfigBuilder(resource);
		
		return builder.build();
	}
}
