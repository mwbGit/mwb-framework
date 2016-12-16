package com.mwb.framework.scheduler;

public abstract class AbstractJobTaskExecutionHandler implements Runnable {
	private JobTaskExecutionNotification notifications;

	private ThreadLocal<JobTaskExecutionNotification> threadLocalNotifications;
		
	public void setNotifications(JobTaskExecutionNotification notifications) {
		this.notifications = notifications;
	}
	
	public void setThreadLocalNotifications(JobTaskExecutionNotification notification) {
		if (threadLocalNotifications == null) {
			threadLocalNotifications = new ThreadLocal<JobTaskExecutionNotification>();
		}
		threadLocalNotifications.set(notification);
	}
	
	@Override
	public void run() {
		setThreadLocalNotifications(notifications);
		handleNotification(threadLocalNotifications.get());
		threadLocalNotifications.remove();
	}
	
	public abstract void handleNotification(JobTaskExecutionNotification notification);

}
