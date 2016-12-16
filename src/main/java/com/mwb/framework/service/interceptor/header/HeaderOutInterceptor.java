package com.mwb.framework.service.interceptor.header;

import com.mwb.framework.log.Log;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HeaderOutInterceptor extends AbstractPhaseInterceptor<Message> {

	private static final Log LOG= Log.getLog(HeaderOutInterceptor.class);
	
	@Value("${server.name:platform}")
	private String serverName;
	
	private static final String HEADER_HOST_NAME = "shbj-host-name";
	
	private static final String HEADER_SERVER_NAME = "shbj-server-name";
	
	
	public HeaderOutInterceptor() {
		super(Phase.PREPARE_SEND);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		try {
			
			Map<String, List<String>> protocolHeaders = CastUtils.cast((Map<?, ?>)message.get(Message.PROTOCOL_HEADERS)); 
			if (protocolHeaders == null) {
	            protocolHeaders = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
	            message.put(Message.PROTOCOL_HEADERS, protocolHeaders);
	        }
			
			String hostName = InetAddress.getLocalHost().getHostName();
			
			List<String> hostHeader = new ArrayList<String>();
			hostHeader.add(hostName);
            protocolHeaders.put(HEADER_HOST_NAME, hostHeader);
            
            List<String> serverHeader = new ArrayList<String>();
            serverHeader.add(StringUtils.isEmpty(serverName) ? "" : serverName);
            protocolHeaders.put(HEADER_SERVER_NAME, serverHeader);
			
		} catch (Exception e) {
			LOG.error("HeaderOutInterceptor error! ", e);
		}
		
	}

}
