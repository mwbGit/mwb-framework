package com.mwb.framework.scheduler;

import com.mwb.framework.log.Log;
import com.mwb.framework.scheduler.quartz.AbstractStatefulQuartzJobDetail;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.quartz.CronTrigger;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public abstract class AbstractStatefulJobTask extends AbstractStatefulQuartzJobDetail implements ISchedulerTask {
	private static final long serialVersionUID = 1L;
	
	private static final Log LOG = Log.getLog(AbstractStatefulJobTask.class);
	
	private List<AbstractJobTaskExecutionHandler> jobTaskExecutionNotifications;
	
	private String alias; // 任务别名
	
	private int lastExecutionDuration;

	private boolean manualSupported = true; // 是否支持手动执行
	
	private boolean persistentSupported = true; // 执行结果是否支持持久化
	
	@Autowired
	private JobTaskNotificationThreadPool jobTaskNotificationThreadPool;
	
	public void execute(JobExecutionContext context) {
		long startTime = System.currentTimeMillis();
		JobTaskExecutionNotification notification = getJobTaskExecutionNotification(context.getTrigger());
		
		try {
			run();
			notification.setLastExecutionSucess(true);
		} catch(Exception e) {
			notification.setLastExecutionSucess(false);
			notification.setException(e.getMessage());
			LOG.error("Job task {} run failed!", notification.getName());
		} finally {
			long endTime = System.currentTimeMillis();
			this.lastExecutionDuration = ((int)(endTime - startTime)); // 毫秒
			
			sendNotification(notification);
		}
		
	}

	private JobTaskExecutionNotification getJobTaskExecutionNotification(Trigger currentTrigger) {
		JobTaskExecutionNotification ser = new JobTaskExecutionNotification();
		
		ser.setAlias(this.getAlias());
		ser.setName(this.getName());
		ser.setGroupName(this.getGroup());
		ser.setClassName(this.getClass().getName());
		ser.setDescription(this.getDescription());
		ser.setLastExecutionDuration(this.getLastExecutionDuration());
		ser.setExecuteTime(new Date());
		ser.setManualSupported(manualSupported);
		ser.setPersistentSupported(persistentSupported);
		
		if (currentTrigger instanceof CronTrigger) { 
			// 保存CronTrigger类型定时任务的cron表达式
			String cronExpression = ((CronTrigger) currentTrigger).getCronExpression();
			ser.setCronExpression(cronExpression);
		} else {
			ser.setCronExpression("--");
		}
		
		return ser;
	}
	
	private void sendNotification(JobTaskExecutionNotification notification) {
		for(AbstractJobTaskExecutionHandler jteNotification : jobTaskExecutionNotifications) {
			jteNotification.setNotifications(notification);
			jobTaskNotificationThreadPool.execute(jteNotification);
		}
	}
	
	public void setManualSupported(boolean manualSupported) {
		this.manualSupported = manualSupported;
	}
	
	public void setPersistentSupported(boolean persistentSupported) {
		this.persistentSupported = persistentSupported;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public boolean isManualSupported() {
		return manualSupported;
	}

	public boolean isPersistentSupported() {
		return persistentSupported;
	}

	public int getLastExecutionDuration() {
		return lastExecutionDuration;
	}

	public void setJobTaskExecutionNotifications(
			List<AbstractJobTaskExecutionHandler> jobTaskExecutionNotifications) {
		this.jobTaskExecutionNotifications = jobTaskExecutionNotifications;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
