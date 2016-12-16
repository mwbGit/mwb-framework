package com.mwb.framework.task;

import com.mwb.framework.log.Log;
import com.mwb.framework.model.task.Task;
import com.mwb.framework.model.task.TaskFailHistory;
import com.mwb.framework.model.task.TaskType;
import com.mwb.framework.scheduler.AbstractStatefulJobTask;
import com.mwb.framework.task.handler.TaskManager;
import com.mwb.framework.util.StringUtility;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class TaskTableJobTask extends AbstractStatefulJobTask {
	
	private static final long serialVersionUID = 1L;

	private static final Log LOG = Log.getLog(TaskTableJobTask.class);
	
	@Autowired
    private TaskManager taskManager;
	
	private TaskExecutor taskExecutor;
    
    public TaskTableJobTask(TaskExecutor taskExecutor) throws Exception {
        this.taskExecutor = taskExecutor;
    }
    	
	@Override
	public void run() {
    	LOG.info("Start TaskTableJobTask.");
		long time1 = System.currentTimeMillis();
    	
        try {
            Date currentTime = new Date();
            
            List<Task> tasks = taskManager.selectAllTaskForExecution(currentTime);
            
            if ((tasks != null) && (tasks.size() > 0)) {
                for (Task task : tasks) {
                	currentTime = new Date();
                	 
                	if ((task.getEndTime() != null)
                    		&& currentTime.after(task.getEndTime())) {
                    	createTaskErrorHistory(task, new Exception("task endTime is bigger more than current time"));
                    	continue;
                    }
                	
                   
                    int updatedRowCount = taskManager.getTaskDao().setTaskInProgress(
                            task.getId(), task.getTimestamp(), currentTime.getTime());
                    
                    // 更新is_in_progress状态时，timestamp字段也更新了，bean中数据需同步
                    task.setTimestamp(currentTime.getTime());
                    
                    if (updatedRowCount > 0) {
                    	try {
                    		RunnableTask runnableTask = new RunnableTask(task);
                    		taskExecutor.execute(runnableTask);
                    		
                    	} catch (Exception e) {
                            LOG.error("RunnableTask.run, Catch an Exception!", e);
                            processError(task, e);
                        }
                    	
                    }                    
                }
            }
            
        } catch (Exception e) {
            LOG.error("Catch an Exception!", e);
        }
        
        LOG.info("End TaskTableJobTask use time {} ms.", System.currentTimeMillis() - time1);
    }

	private void processSuccess(Task task)  {
		try {
			taskManager.getTaskDao().deleteTask(task.getId());
		} catch (SQLException e) {
			LOG.error("Catch an Exception in TaskTableJobTask.processSuccess!", e);
		}
    }
    
	private void processError(Task task, Exception exception) {
		Date nextTime = null;
		Date now = new Date();
		
		TaskType taskType = task.getType();
		String[] intervalsStr =  taskType.getTimeIntervals().split(",");
		Integer[] intervals = getIntIntervals(intervalsStr);
		
		if (intervals == null 
				|| intervals.length <= 0
				|| task.getFailureCount() >= intervals.length) {
			
			try {
				createTaskErrorHistory(task, exception);
			} catch (SQLException e) {
				LOG.error("Catch an Exception in TaskTableJobTask.processError!", e);
			}
			return;
		}
		
		Integer syncupIntervalTime = Integer.valueOf(intervals[task.getFailureCount()]);
		nextTime = DateUtils.addMinutes(now, syncupIntervalTime);
		try {
			taskManager.getTaskDao().updateTaskFailure(task.getId(), nextTime, now.getTime(), task.getTimestamp());
		} catch (SQLException e) {
			LOG.error("Catch an Exception in TaskTableJobTask.processError!", e);
		}
    }
    
	private Integer[] getIntIntervals(String[] intervalsStr) {
    	Integer[] res = new Integer[intervalsStr.length];
    	for (int i = 0; i < intervalsStr.length; i++) {
    		String string = intervalsStr[i].trim();
    		res[i] = Integer.valueOf(string);
    	}
    	
    	return res;
    }
    private class RunnableTask implements Runnable {
    	private final Task task;
    	
    	public RunnableTask(Task task) {
    		this.task = task;
    	}

		@Override
		public void run() {
			try {
				taskManager.execute(task);
				processSuccess(task);
			} catch (Exception e) {
				LOG.error("Catch an Exception!", e);
				processError(task, e);
			}
		}
    }
    
    private void createTaskErrorHistory(Task task, Exception exception) throws SQLException {
    	String content = String.format("taskContent:%s, taskType:%S.", task.getContentStr(), task.getType().getDescription());
    	LOG.info("create task error history. {}", content);
    	
    	String error = StringUtility.getLimitLengthString(StringUtility.getStackTraceString(exception), 1024);
		TaskFailHistory taskFailHistory = new TaskFailHistory();
		taskFailHistory.setContent(task.getContentStr());
		taskFailHistory.setCreateTime(new Date());
		taskFailHistory.setEndTime(task.getEndTime());
		taskFailHistory.setFailureCount(task.getFailureCount());
		taskFailHistory.setType(task.getType());
		taskFailHistory.setErrorLog(error);
		taskManager.getTaskDao().insertTaskFailHistory(taskFailHistory);
		
		taskManager.getTaskDao().deleteTask(task.getId());
	}
}
