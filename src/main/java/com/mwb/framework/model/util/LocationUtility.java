package com.mwb.framework.model.util;

import com.mwb.framework.model.Location;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class LocationUtility {
	// 定义ROOT的父节点code
	public static final String ROOT_PARENT_CODE = "0";

	public static final int ROOT_CODE_LEN = 2;
	public static final int PROVINCE_CODE_LEN = 4;
	public static final int CITY_CODE_LEN = 6;
	public static final int BIZ_AREA_CODE_LEN = 10;

	public static String getParentCode(String locationCode) {
		if (StringUtils.isBlank(locationCode) || isRoot(locationCode)) {
			return null;
		} else {
			return locationCode.substring(0, locationCode.length() - 2);
		}
	}

	public static String getProvinceCode(String locationCode) {
		if (StringUtils.isNotBlank(locationCode) && locationCode.length() >= PROVINCE_CODE_LEN) {
			return locationCode.substring(0, PROVINCE_CODE_LEN);
		}

		return null;
	}

	public static String getCityCode(String locationCode) {
		if (StringUtils.isNotBlank(locationCode) && locationCode.length() >= CITY_CODE_LEN) {
			return locationCode.substring(0, CITY_CODE_LEN);
		}

		return null;
	}

	public static String getBusinessAreaCode(String locationCode) {
		if (StringUtils.isNotBlank(locationCode) && locationCode.length() >= BIZ_AREA_CODE_LEN) {
			return locationCode.substring(0, BIZ_AREA_CODE_LEN);
		}

		return null;
	}

	public static String getProvinceAndHighLevelCode(String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}

		return (code.length() > PROVINCE_CODE_LEN) ? code.substring(0, PROVINCE_CODE_LEN) : code;
	}

	public static String getCityAndHighLevelCode(String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}

		return (code.length() > CITY_CODE_LEN) ? code.substring(0, CITY_CODE_LEN) : code;
	}

	public static String getBusinessAreaAndHighLevelCode(String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}

		return (code.length() > BIZ_AREA_CODE_LEN) ? code.substring(0, BIZ_AREA_CODE_LEN) : code;
	}

	public static boolean isRoot(String locationCode) {
		if (locationCode != null) {
			return (locationCode.length() == ROOT_CODE_LEN);
		}

		return false;
	}

	public static boolean isProvince(String locationCode) {
		if (locationCode != null) {
			return (locationCode.length() == PROVINCE_CODE_LEN);
		}

		return false;
	}

	public static boolean isCity(String locationCode) {
		if (locationCode != null) {
			return (locationCode.length() == CITY_CODE_LEN);
		}

		return false;
	}

	public static boolean isBusinessArea(String locationCode) {
		if (locationCode != null) {
			return (locationCode.length() == BIZ_AREA_CODE_LEN);
		}

		return false;
	}

	public static boolean isParent(String childCode, String parentCode) {
		if (childCode != null) {
			if (parentCode != null && childCode != null) {
				return childCode.startsWith(parentCode);
			}
		}

		return false;
	}

	public static boolean isChild(String parentCode, String childCode) {
		if (parentCode != null) {
			if (childCode != null && parentCode != null) {
				return childCode.startsWith(parentCode);
			}
		}

		return false;
	}

	public static boolean isSibling(Location location, String locationCode) {
		if (location != null) {
			if (locationCode != null && location.getCode() != null) {
				String parent = location.getCode().substring(0, location.getCode().length() - 2);
				if (StringUtils.isNotBlank(parent)) {
					return parent.equals(locationCode.substring(0, locationCode.length() - 2));
				}
			}
		}

		return false;
	}

	// 获取直接上级的code
	public static String getDirectParentCode(String locationCode) {
		if (StringUtils.isNotBlank(locationCode)) {
			if (locationCode.length() == ROOT_CODE_LEN) {
				return ROOT_PARENT_CODE;
			} else if (locationCode.length() > ROOT_CODE_LEN) {
				return locationCode.substring(0, locationCode.length() - 2);
			}
		}

		return null;
	}

	public static List<String> getParentCodes(String locationCode, boolean includeSelf) {
		List<String> codes = new ArrayList<String>();

		String rootCode = getRootCode(locationCode);
		if (StringUtils.isNotBlank(rootCode)) {
			codes.add(rootCode);
		}

		String provinceCode = getProvinceCode(locationCode);
		if (StringUtils.isNotBlank(provinceCode)) {
			codes.add(provinceCode);
		}

		String cityCode = getCityCode(locationCode);
		if (StringUtils.isNotBlank(cityCode)) {
			codes.add(cityCode);
		}

		String businessAreaCode = getBusinessAreaCode(locationCode);
		if (StringUtils.isNotBlank(businessAreaCode)) {
			codes.add(businessAreaCode);
		}

		if (includeSelf) {
			codes.add(locationCode);
		}

		return codes;
	}

	public static String getRootCode(String locationCode) {
		if (StringUtils.isNotBlank(locationCode) && locationCode.length() >= ROOT_CODE_LEN) {
			return locationCode.substring(0, ROOT_CODE_LEN);
		}

		return null;
	}

	public static boolean isContainLocation(List<String> locationCodes, String locationCode) {

		if (locationCodes == null || locationCodes.isEmpty()) {
			return false;
		}

		if (locationCodes.contains(locationCode)) {
			return true;
		}

		for (String parentLocationCode : locationCodes) {
			if (locationCode.startsWith(parentLocationCode)) {
				return true;
			}
		}

		return false;
	}

	public static boolean coverTargetLocation(String[] locations, String target) {
		if (locations != null) {
			return coverTargetLocation(Arrays.asList(locations), target);
		}

		return false;
	}

	public static boolean coverTargetLocation(Collection<String> locations, String target) {
		if (locations != null && StringUtils.isNotBlank(target)) {
			for (String location : locations) {
				if (StringUtils.isNotBlank(location) && target.startsWith(location)) {
					return true;
				}
			}
		}

		return false;
	}
}
