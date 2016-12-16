package com.mwb.framework.http.util;

import com.mwb.framework.log.Log;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class HttpServletUtility {
	private static final Log LOG = Log.getLog(HttpServletUtility.class);
	private static final String USER_AGENT = "user-agent";
	
	public static String getRequestUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int pathParamIndex = uri.indexOf(';');

        if (pathParamIndex > 0) {
            // strip everything after the first semi-colon
            uri = uri.substring(0, pathParamIndex);
        }
        
        return uri;
    }
	
	public static boolean isWeixin(HttpServletRequest request) {
		return isWeixin(request.getHeader(USER_AGENT));
	}
	
	public static boolean isIphone(HttpServletRequest request) {
		return isIphone(request.getHeader(USER_AGENT));
	}
	
	public static boolean isAndroid(HttpServletRequest request) {
		return isAndroid(request.getHeader(USER_AGENT));
	}
	
	public static boolean isWeixin(String userAgent) {
		if (StringUtils.isBlank(userAgent)) {
			return false;
		} else if (userAgent.toLowerCase().indexOf("micromessenger") >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public static boolean isAndroid(String userAgent) {
		if (StringUtils.isBlank(userAgent)) {
			return false;
		} else if (userAgent.toLowerCase().indexOf("android") >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isIphone(String userAgent) {
		if (StringUtils.isBlank(userAgent)) {
			return false;
		} else if (userAgent.toLowerCase().indexOf("iphone") >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isMoblie(HttpServletRequest request) {
		boolean isMoblie = false;
		String[] mobileAgents = { "iphone", "android", "phone", "mobile", "wap", "netfront", "java", "opera mobi",
				"opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod",
				"nokia", "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma",
				"docomo", "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos",
				"techfaith", "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips", "sagem",
				"wellcom", "bunjalloo", "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos",
				"pantech", "gionee", "portalmmm", "jig browser", "hiptop", "benq", "haier", "^lct", "320x320",
				"240x320", "176x220", "w3c ", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq", "bird", "blac",
				"blaz", "brew", "cell", "cldc", "cmd-", "dang", "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs",
				"kddi", "keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi",
				"mot-", "moto", "mwbp", "nec-", "newt", "noki", "oper", "palm", "pana", "pant", "phil", "play", "port",
				"prox", "qwap", "sage", "sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem",
				"smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tsm-", "upg1", "upsi", "vk-v",
				"voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda", "xda-",
				"Googlebot-Mobile" };
		if (request.getHeader("User-Agent") != null) {
			for (String mobileAgent : mobileAgents) {
				if (request.getHeader("User-Agent").toLowerCase().indexOf(mobileAgent) >= 0) {
					isMoblie = true;
					break;
				}
			}
		}
		return isMoblie;
	}
	
	
	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
	    	for (Cookie c : cookies) {
	    		if (c.getName().equals(cookieName)) {
	    			return c.getValue();
	    		} 
	    	}
		}
    	return null;
	}
	
	public static String getUserAgent(HttpServletRequest request) {
		return request.getHeader(USER_AGENT);
	}
	
	public static UserAgentInfo getUserAgentInfo(HttpServletRequest request) {
		String userAgent = request.getHeader(USER_AGENT);
		UserAgentInfo userAgentInfo = new UserAgentInfo();
		
		if (userAgent.toLowerCase().indexOf("firefox") >= 0) {
			int beginIndex = userAgent.toLowerCase().indexOf("firefox");
			String info = userAgent.substring(beginIndex);
			String [] str = info.split("/");
			userAgentInfo.setBrowserType(str[0]);
			userAgentInfo.setBrowserVersion(str[1]);
			userAgentInfo.setUserAgent(userAgent);
		} else if (userAgent.toLowerCase().indexOf("lbbrowser") >= 0) {
			userAgentInfo.setBrowserType("LLBROWSER");
			userAgentInfo.setUserAgent(userAgent);
		} else if (userAgent.toLowerCase().indexOf("360") >= 0) {
			userAgentInfo.setBrowserType("360");
			userAgentInfo.setUserAgent(userAgent);
		} else if (userAgent.toLowerCase().indexOf("metasr") >= 0) {
			int beginIndex = userAgent.toLowerCase().indexOf("metasr");
			String info = userAgent.substring(beginIndex);
			String [] str = info.split(" ");
			userAgentInfo.setBrowserType(str[0]);
			userAgentInfo.setBrowserVersion(str[1]);
			userAgentInfo.setUserAgent(userAgent);
		} else if (userAgent.toLowerCase().indexOf("tencenttraveler") >= 0) {
			int beginIndex = userAgent.toLowerCase().indexOf("tencenttraveler");
			String info = userAgent.substring(beginIndex);
			String [] str1 = info.split(";");
			String [] str2 = str1[0].split(" ");
			userAgentInfo.setBrowserType(str2[0]);
			userAgentInfo.setBrowserVersion(str2[1]);
			userAgentInfo.setUserAgent(userAgent);
		}  else if (userAgent.toLowerCase().indexOf("maxthon") >= 0) {
			int beginIndex = userAgent.toLowerCase().indexOf("maxthon");
			String info = userAgent.substring(beginIndex);
			String [] str1 = info.split(" ");
			String [] str2 = str1[0].split("/");
			userAgentInfo.setBrowserType(str2[0]);
			//某些特殊maxthon浏览器，无法得到版本号，加此判断
			if (str2.length > 1) {
				userAgentInfo.setBrowserVersion(str2[1]);
			}
			userAgentInfo.setUserAgent(userAgent);
		} else if (userAgent.toLowerCase().indexOf("ubrowser") >= 0) {
			int beginIndex = userAgent.toLowerCase().indexOf("ubrowser");
			String info = userAgent.substring(beginIndex);
			String [] str1 = info.split(" ");
			String [] str2 = str1[0].split("/");
			userAgentInfo.setBrowserType(str2[0]);
			userAgentInfo.setBrowserVersion(str2[1]);
			userAgentInfo.setUserAgent(userAgent);
		} else if (userAgent.toLowerCase().indexOf("applewebkit") >= 0 && userAgent.toLowerCase().indexOf("chrome") >= 0
				&&userAgent.toLowerCase().indexOf("safari") >= 0) {
			int beginIndex = userAgent.toLowerCase().indexOf("chrome");
			String info = userAgent.substring(beginIndex);
			String [] str1 = info.split(" ");
			String [] str2 = str1[0].split("/");
			userAgentInfo.setBrowserType(str2[0]);
			userAgentInfo.setBrowserVersion(str2[1]);
			userAgentInfo.setUserAgent(userAgent);
		} else if (userAgent.toLowerCase().indexOf("applewebkit") >= 0 && userAgent.toLowerCase().indexOf("safari") >= 0) {
			int beginIndex = userAgent.toLowerCase().indexOf("safari");
			String info = userAgent.substring(beginIndex);
			String [] str1 = info.split("/");
			userAgentInfo.setBrowserType(str1[0]);
			userAgentInfo.setBrowserVersion(str1[1]);
			userAgentInfo.setUserAgent(userAgent);
		} else if (userAgent.toLowerCase().indexOf("compatible") >= 0 && userAgent.toLowerCase().indexOf("msie") >= 0) {
			int beginIndex = userAgent.toLowerCase().indexOf("msie");
			String info = userAgent.substring(beginIndex);
			String [] str1 = info.split(";");
			String [] str2 = str1[0].split(" ");
			userAgentInfo.setBrowserType(str2[0]);
			userAgentInfo.setBrowserVersion(str2[1]);
			userAgentInfo.setUserAgent(userAgent);
		} else {
			userAgentInfo.setBrowserType("other");
			userAgentInfo.setBrowserVersion("other");
			userAgentInfo.setUserAgent(userAgent);
		}		
		return userAgentInfo; 
	}
	
	public static String getIpAddress(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		LOG.debug("IP address in x-forwarded-for:{}, x-real-ip:{}, remoteaddress:{}", 
				request.getHeader("x-forwarded-for"), request.getHeader("x-real-ip"), request.getRemoteAddr());
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-real-ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	public static String produceSignKey(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();

		Set<String> keys = map.keySet();
		int index = 0;
		for (String key : keys) {
			sb.append(key).append("=").append(map.get(key));
			index ++;
			if (index < keys.size()) {
				sb.append("&");
			}
		}
		
		return DigestUtils.md5Hex(sb.toString());
	}
	
	public static String produceSignKey(Map<String, String> map, String secretKey) {
		StringBuilder sb = new StringBuilder();

		Set<String> keys = map.keySet();
		for (String key : keys) {
			sb.append(key).append("=").append(map.get(key)).append("&");
		}
		sb.append("secretKey=").append(secretKey);
		
		return DigestUtils.md5Hex(sb.toString());
	}
	public static Map<String, String> getParamMap(HttpServletRequest request) throws UnsupportedEncodingException {
		Map<String, String> params = new TreeMap<String, String>();
		@SuppressWarnings("unchecked")
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			String value = request.getParameter(paramName);
			if (value == null) {
				value = "";
			}
			params.put(paramName, value.trim());
		}
		return params;
	}
}
