package com.mwb.framework.util;

import java.util.Collection;

public class CollectionUtility {

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
	
	public static String toString(Collection<?> collection) {
		return toString(collection, ",");
	}
	
	public static String toString(Collection<?> collection, String seperator) {
		if (collection == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Object obj : collection) {
			if (i++ > 0) {
				sb.append(seperator);
			}
			sb.append(obj.toString());			
		}
		return sb.toString();
	}
}
