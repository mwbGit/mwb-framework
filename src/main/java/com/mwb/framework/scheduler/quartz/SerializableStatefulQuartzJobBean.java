package com.mwb.framework.scheduler.quartz;

import com.mwb.framework.scheduler.AbstractStatefulJobTask;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class SerializableStatefulQuartzJobBean extends QuartzJobBean implements StatefulJob {
	private ApplicationContext applicationContext;

	private String taskName;

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		
		AbstractStatefulJobTask task = (AbstractStatefulJobTask)applicationContext.getBean(taskName);
		task.execute(context);
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
}
