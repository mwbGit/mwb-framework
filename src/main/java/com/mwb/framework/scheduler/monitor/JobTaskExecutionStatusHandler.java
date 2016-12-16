package com.mwb.framework.scheduler.monitor;

import com.mwb.framework.log.Log;
import com.mwb.framework.scheduler.AbstractJobTaskExecutionHandler;
import com.mwb.framework.scheduler.JobTaskExecutionNotification;
import com.mwb.framework.scheduler.dao.JobTaskExecutionStatus;
import com.mwb.framework.scheduler.dao.JobTaskExecutionStatusDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;

public class JobTaskExecutionStatusHandler extends AbstractJobTaskExecutionHandler {

	private static final Log LOG = Log.getLog(JobTaskExecutionStatusHandler.class);
	
	@Autowired
	private JobTaskExecutionStatusDao schedulerExecutionDao;
	
	@Override
	public void handleNotification(JobTaskExecutionNotification notification) {
		
		if (notification.isPersistentSupported()) {
			try {
				schedulerExecutionDao.insertOrUpdateJobTaskExecutionStatus(new JobTaskExecutionStatus(notification));
				LOG.info("Persistent task to DB:{}.", notification);
			} catch (SQLException e) {
				LOG.error("Persistent task to DB error:{}.", e.getMessage());
			}
		}
	}

}
