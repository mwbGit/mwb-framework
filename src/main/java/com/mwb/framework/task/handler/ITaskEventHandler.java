package com.mwb.framework.task.handler;

import com.mwb.framework.model.task.Task;

public interface ITaskEventHandler {
	
	public void handle(Task task) throws Exception;
	
	public String getTaskType();
}
