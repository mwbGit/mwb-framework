package com.mwb.framework.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JobTaskNotificationThreadPool {
	private ExecutorService threadPool;
	
	public JobTaskNotificationThreadPool(int threadNumber) {
		threadPool  = new ThreadPoolExecutor(threadNumber, threadNumber, 10000, TimeUnit.MILLISECONDS, 
				new LinkedBlockingQueue<Runnable>(100), new ThreadPoolExecutor.DiscardPolicy());
	}
	
	public void execute(Runnable thread) {
		threadPool.execute(thread);
	}
}
