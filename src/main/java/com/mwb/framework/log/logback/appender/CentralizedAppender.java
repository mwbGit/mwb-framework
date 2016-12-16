package com.mwb.framework.log.logback.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.net.SocketAppender;
import ch.qos.logback.classic.spi.*;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import org.slf4j.Marker;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class CentralizedAppender extends SocketAppender {

	private static final PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformerWithProperties();
	
	private static Map<String, String> propertyMap;
	
	private boolean enabled = false;
	
	public CentralizedAppender() throws UnknownHostException {
		propertyMap = new HashMap<String, String>();
		propertyMap.put("hostName", InetAddress.getLocalHost().getHostName());
	}
	
	@Override
	public void start() {
		if (enabled) {
			super.start();
		}
	}
	
	@Override
	public PreSerializationTransformer<ILoggingEvent> getPST() {
        return pst;
    }
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setClientName(String clientName) {
		propertyMap.put("clientName", clientName);
	}
	
	private static class LoggingEventPreSerializationTransformerWithProperties implements PreSerializationTransformer<ILoggingEvent> {

		@Override
		public Serializable transform(ILoggingEvent event) {
			if (event == null) {
	            return null;
	        }
	        if (event instanceof LoggingEvent) {
	            return LoggingEventVO.build(new LoggingEventAdapter(event));
	        } else if (event instanceof LoggingEventVO) {
	            return (LoggingEventVO) event;
	        } else {
	            throw new IllegalArgumentException("Unsupported type " + event.getClass().getName());
	        }
		}
		
	}
	
	private static class LoggingEventAdapter extends LoggingEvent {
		
		private ILoggingEvent event;
		
		public LoggingEventAdapter(ILoggingEvent event) {
			this.event = event;
		}
		
		@Override
		public String getLoggerName() {
			return event.getLoggerName();
		}
		
		@Override
		public LoggerContextVO getLoggerContextVO() {
			return event.getLoggerContextVO();
		}

		@Override
	    public IThrowableProxy getThrowableProxy() {
			return event.getThrowableProxy();
	    }
	    
		@Override
	    public String getThreadName() {
			return event.getThreadName();
	    }

		@Override
	    public Level getLevel() {
			return event.getLevel();
	    }

		@Override
	    public String getMessage() {
			return event.getMessage();
	    }

		@Override
	    public Object[] getArgumentArray() {
			return event.getArgumentArray();
	    }
	    
		@Override
	    public StackTraceElement[] getCallerData() {
			return event.getCallerData();
	    }
	    
		@Override
	    public boolean hasCallerData() {
			return event.hasCallerData();
	    }

		@Override
	    public Marker getMarker() {
			return event.getMarker();
	    }
	    
		@Override
	    public Map<String, String> getMDCPropertyMap() {
			Map<String, String> originMap = event.getMDCPropertyMap();
			if (originMap.isEmpty()) {
				return CentralizedAppender.propertyMap;
			}
			
			originMap.putAll(CentralizedAppender.propertyMap);
			
			return originMap;
	    }
	    
		@Override
	    public long getTimeStamp() {
	    	return event.getTimeStamp();
	    }	    
	}
}
