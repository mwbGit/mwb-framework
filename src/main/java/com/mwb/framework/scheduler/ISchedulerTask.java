package com.mwb.framework.scheduler;

public interface ISchedulerTask {
	/**
	 * 定时任务需要实现的业务逻辑方法
	 * 注意：对此方法的实现不需要catch异常，此方法的调用者会捕获异常
	 * @throws Exception
	 */
	public void run() throws Exception;
	
}
