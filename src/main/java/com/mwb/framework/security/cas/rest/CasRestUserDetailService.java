package com.mwb.framework.security.cas.rest;

import com.mwb.framework.http.client.AbstractHttpClient;
import com.mwb.framework.log.Log;
import com.mwb.framework.security.IUserDetailsBuilder;
import com.mwb.framework.security.cas.CasServiceProperties;
import com.mwb.framework.util.XmlUtility;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;


public class CasRestUserDetailService extends AbstractHttpClient implements ICasRestUserDetailService {

	private final static Log LOG = Log.getLog(CasRestUserDetailService.class);
	
	private String casServerUrl;
	private String casClientUrl;
	
	private IUserDetailsBuilder userDetailsBuilder;
	
	/**
	 * 验证TGT，如果成功返回ST
	 * 
	 * @param token
	 * @return
	 */
	@Override
	public String verifyToken(String token) {
		try {
			String logoutUrl = getLogoutUrl(token);
			
			if (StringUtils.isBlank(logoutUrl)) {
				return null;
			}
			
			Map<String, String> params = new HashMap<String, String>();
			
			params.put("service", logoutUrl);
		
			// 验证TGT
			String response1 = post(casServerUrl + "/v1/tickets/" + token,  params, null);
			LOG.debug("TGT validate response is {}.", response1);
			
			// 解析TGT验证结果
			if (response1.contains("TicketGrantingTicket could not be found.")) {
				LOG.error("Token {} is invalid.", token);
	
				return null;
			}
			
			return response1;
			
		} catch (Exception e) {
			LOG.error("Catch an exception!", e);
			
			return null;
		}
	}
	
	@Override
	public UserDetails loadUserDetails(String token, String ticket) {
		try {
			String logoutUrl = getLogoutUrl(token);
			
			if (StringUtils.isBlank(logoutUrl)) {
				return null;
			}
			
			// 验证ST
			Map<String, String> params = new HashMap<String, String>();
			params.put("service", logoutUrl);
			params.put("ticket", ticket);
			
			String response2 = get(casServerUrl + "/serviceValidate", params, null);
			LOG.debug("ST validate response is {}.", response2);
			
			// 解析验证ST结果
			String error = XmlUtility.getTextForElement(response2, "authenticationFailure");
			if (StringUtils.isNotBlank(error)) {
				LOG.error("ST is invalid, ST={}", ticket);
				
				return null;
	        }
	
			Map<String, Object> attributes = extractCustomAttributes(response2);
			
			return (userDetailsBuilder != null) ? userDetailsBuilder.build(attributes) : null;
			
		} catch (Exception e) {
			LOG.error("Catch an exception!", e);
			
			return null;
		}
	}

	/*
	 * return logout url used by CAS server
	 */
	private String getLogoutUrl(String token) {
		StringBuffer sb = new StringBuffer();
		sb.append(casClientUrl)
			.append(CasRestConstants.CAS_LOGOUT_URL)
			.append("?token=")
			.append(token);
		
		return sb.toString();
	}

	private Map<String, Object> extractCustomAttributes(final String xml) throws IOException {
    	final int pos1 = xml.indexOf("<cas:attributes>");
    	final int pos2 = xml.indexOf("</cas:attributes>");
    	
    	if (pos1 == -1) {
    		return Collections.emptyMap();
    	}
    	
    	final String attributesText = xml.substring(pos1+16, pos2);
    	
    	final Map<String, Object> attributes = new HashMap<String, Object>();
    	final BufferedReader br = new BufferedReader(new StringReader(attributesText));
    	
    	String line;
    	final List<String> attributeNames = new ArrayList<String>();
    	
    	while ((line = br.readLine()) != null) {
    		final String trimmedLine = line.trim();
    		if (trimmedLine.length() > 0) {
    			final int leftPos = trimmedLine.indexOf(":");
    			final int rightPos = trimmedLine.indexOf(">");
    			attributeNames.add(trimmedLine.substring(leftPos+1, rightPos));
    		}
    	}

    	br.close();
    	
    	for (String name: attributeNames) {
    		attributes.put(name, XmlUtility.getTextForElement(xml, name));
    	}
    	
    	return attributes;
    }

	public void setServiceProperties(CasServiceProperties serviceProperties) {
		this.casServerUrl = serviceProperties.getCasServerUrl();
		this.casClientUrl = serviceProperties.getCasClientUrl();
	}
	
	public void setUserDetailsBuilder(IUserDetailsBuilder userDetailsBuilder) {
		this.userDetailsBuilder = userDetailsBuilder;
	}
}
