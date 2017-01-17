package com.mwb.framework.util;

import com.mwb.framework.api.service.rs.api.ServiceResponse;
import com.mwb.framework.service.StatusCodeManager;

public class ServiceResponseInspector {
	
	public static void checkResponseSuccess(ServiceResponse response) throws Exception {
		if (response == null) {
			throw new Exception("System Exception (response is null)");
		}
		if (!StatusCodeManager.getSucceedCode().equals(response.getResultCode())) {
			throw new Exception(response.getResultMessage());
		}
	}
	
}
