package com.mwb.framework.event;

import java.util.List;

public class AlertEvent extends AbstractBaseEvent {
	private static final long serialVersionUID = 1L;
	//properties only for Email
    private String subject;
    private Exception exception;
    private String mailList;
    
    //common properties
    private String content;
    private String templateCode;
    private List<String> params;
    
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMailList() {
		return mailList;
	}
	public void setMailList(String mailList) {
		this.mailList = mailList;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public String getTemplateCode() {
		return templateCode;
	}
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	public List<String> getParams() {
		return params;
	}
	public void setParams(List<String> params) {
		this.params = params;
	}
	
	
}
