package com.mwb.framework.api.service.rs.api;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;


public class ServiceResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String resultCode;
    
    @XmlElement
    private String resultMessage;
    
    // 为每一个错误产生一个临时近似唯一的ID，这个ID会记录到日志里面，方便错误定位
    @XmlElement
    private String errorId;
    
    public ServiceResponse() {};
    
    public ServiceResponse(String resultCode) {
    	this.resultCode = resultCode;
    }

    public String getCode() {
        return resultCode;
    }

    public void setCode(String code) {
        this.resultCode = code;
    }

	public String getMessage() {
		return resultMessage;
	}

	public void setMessage(String message) {
		this.resultMessage = message;
	}
    
	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public String getErrorId() {
		return errorId;
	}

	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}

}
