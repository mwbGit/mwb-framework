package com.mwb.framework.scheduler.quartz;

import com.mwb.framework.log.Log;
import com.mwb.framework.scheduler.AbstractJobTaskExecutionHandler;
import com.mwb.framework.scheduler.JobTaskExecutionNotification;

public class JobTaskFailureHandler extends AbstractJobTaskExecutionHandler {

    private static final Log LOG = Log.getLog(JobTaskFailureHandler.class);
	
	@Override
	public void handleNotification(JobTaskExecutionNotification notification) {
		if(!notification.isLastExecutionSucess()) {
			LOG.error("DefaultFailureNotification -- scheduler execution result:{}.", notification);
		}
	}
}
