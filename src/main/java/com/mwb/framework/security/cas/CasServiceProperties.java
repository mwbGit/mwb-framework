package com.mwb.framework.security.cas;

import com.mwb.framework.log.Log;
import org.springframework.security.cas.ServiceProperties;

public class CasServiceProperties extends ServiceProperties {
	private final static Log LOG = Log.getLog(CasServiceProperties.class);
	
	private String filterProcessesUrl;
	private String casServerUrl;
	private String casLoginUrl;
	private String casLogoutUrl;
	private String casClientUrl;
	
	public CasServiceProperties() {
		setSendRenew(false);
		setAuthenticateAllArtifacts(true);
	}
	
	public String getFilterProcessesUrl() {
		return filterProcessesUrl;
	}
	
	public void setFilterProcessesUrl(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
		
		StringBuffer sb = new StringBuffer();
    	sb.append(getCasClientUrl()).append(filterProcessesUrl);
    	
    	String finalServceUrl = sb.toString();
    	LOG.debug("Service url : {}", finalServceUrl);
    	
    	setService(finalServceUrl);
	}
	
	public String getCasServerUrl() {
		return casServerUrl;
	}

	public void setCasServerUrl(String casServerUrl) {
		this.casServerUrl = casServerUrl;
		this.casLoginUrl = casServerUrl + "/login";
		this.casLogoutUrl = casServerUrl + "/logout";
	}

	public String getCasClientUrl() {
		return casClientUrl;
	}

	public void setCasClientUrl(String casClientUrl) {
		this.casClientUrl = casClientUrl;
	}
	
	public String getCasLoginUrl() {
		return casLoginUrl;
	}

	public String getCasLogoutUrl() {
		return casLogoutUrl;
	}
}
