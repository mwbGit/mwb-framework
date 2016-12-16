package com.mwb.framework.service.interceptor.header;

import com.mwb.framework.context.RequestContextAccessor;
import com.mwb.framework.log.Log;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import java.util.List;
import java.util.Map;

public class HeaderInInterceptor extends AbstractPhaseInterceptor<Message> {

	private static final Log LOG= Log.getLog(HeaderInInterceptor.class);
	
	private static final String HEADER_HOST_NAME = "shbj-host-name";
	
	private static final String HEADER_SERVER_NAME = "shbj-server-name";

	public HeaderInInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		try {
			if (isRequestor(message)) {
				// 如果是CXF client直接返回不处理
				return; 	
			}
			
			String hostName = null;
			String serverName = null;
			Map<String, List<String>> protocolHeaders = CastUtils.cast((Map<?, ?>)message.get(Message.PROTOCOL_HEADERS)); 
			
			if (protocolHeaders != null) {
				if (protocolHeaders.get(HEADER_HOST_NAME) != null) {
					hostName = protocolHeaders.get(HEADER_HOST_NAME).get(0);
				}
				
				if (protocolHeaders.get(HEADER_SERVER_NAME) != null) {
					serverName = protocolHeaders.get(HEADER_SERVER_NAME).get(0);
				}
			}

			RequestContextAccessor.setInternalRemoteHostName(hostName);
			RequestContextAccessor.setInternalRemoteServerName(serverName);
			
		} catch (Exception e) {
			LOG.error("HeaderInInterceptor error! ", e);
		}
		
	}
}
