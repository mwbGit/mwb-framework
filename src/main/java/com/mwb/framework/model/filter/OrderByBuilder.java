package com.mwb.framework.model.filter;

import com.mwb.framework.log.Log;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.util.*;

public class OrderByBuilder implements ResourceLoaderAware, InitializingBean  {
	private static final Log LOG = Log.getLog(OrderByBuilder.class);
	
	private static final Map<String, Map<String, String>> MAPPINGS;
	
	static {
		MAPPINGS = new HashMap<String, Map<String, String>>();
	}

	private ResourceLoader resourceLoader;
	private String configFile;
	
	public static String getOrderByString(SearchFilter filter) {
		if (filter.isOrdered()) {
			List<OrderingProperty> orderingProperties = filter.getOrderingProperties();
			
			if (orderingProperties == null || orderingProperties.size() == 0) {
				return null;
			}
			
			String filterName = filter.getClass().getName();
			Map<String, String> mapping = MAPPINGS.get(filterName);
			
			if (mapping == null || mapping.size() == 0) {
				return null;
			}
			
			Collections.sort(orderingProperties, new Comparator<OrderingProperty>() {
				@Override
				public int compare(OrderingProperty o1, OrderingProperty o2) {
					return (o1.getPriority() - o2.getPriority());
				}
	            
	        });
			
			StringBuilder sb = new StringBuilder();
			for (OrderingProperty orderingProperty : orderingProperties) {
				String propertyName = orderingProperty.getProperty();
				String column = mapping.get(propertyName);
				
				if (StringUtils.isBlank(column)) {
					continue;
				}
				
				if (sb.length() > 0) {
					sb.append(", ");
				} 
				
				sb.append(column);
				
				if (orderingProperty.isAsc()) {
					sb.append(" ASC");
				} else {
					sb.append(" DESC");
				}
			}
			
			return sb.toString();
		}
		
		return null;
	}
	
	private void init(InputStream is) throws Exception {
		Builder parser = new Builder();
		Document doc = parser.build(is);
		
		Element root = doc.getRootElement();
		
		Elements filters = root.getChildElements();
		
		for (int i = 0; i < filters.size(); i ++) {
			Element filter = filters.get(i);
			
			String name = filter.getAttributeValue("name");
			
			Map<String, String> mapping = new HashMap<String, String>();
			
			MAPPINGS.put(name, mapping);
			
			Elements properties = filter.getChildElements();
			
			for (int j = 0; j < properties.size(); j ++) {
				Element property = properties.get(j);
				
				String propertyName = property.getAttributeValue("name");
				String column = property.getAttributeValue("column");
				
				mapping.put(propertyName, column);
			}
		}
		
		LOG.info("Load {} complete -\n{}", root.toXML());
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			Resource resource = resourceLoader.getResource(configFile);
			
			InputStream is = resource.getInputStream();
			
			if (is == null) {
				LOG.warn("Cannot load configuration file!");
				
				return;
			}
			
			init(is);
		} catch(Exception e) {
			LOG.warn("Catch an exception when loading configuration file!");
		}
	}
}
