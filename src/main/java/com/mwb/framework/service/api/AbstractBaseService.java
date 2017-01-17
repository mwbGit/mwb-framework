package com.mwb.framework.service.api;

import com.mwb.framework.api.service.rs.api.ServiceResponse;
import com.mwb.framework.service.StatusCodeManager;


public abstract class AbstractBaseService {

    protected void setStatusCode(ServiceResponse response, String code) {
        response.setResultCode(code);
        response.setResultMessage(StatusCodeManager.getMessage(code));
    }

}
