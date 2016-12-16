package com.mwb.framework.task.handler;

import com.mwb.framework.event.AbstractBaseEvent;
import com.mwb.framework.event.AlertEvent;
import com.mwb.framework.event.EventManager;
import com.mwb.framework.event.IEventHandler;
import com.mwb.framework.log.Log;
import com.mwb.framework.model.task.Task;
import com.mwb.framework.model.task.TaskType;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

public abstract class AbstractTaskEventHander implements IEventHandler {

	private static final Log LOG = Log.getLog(AbstractTaskEventHander.class);
    private static int DEFAULT_SEQUENCE = 10;
    
	@Autowired
	private TaskManager taskManager;

	@Override
	public void process(AbstractBaseEvent baseEvent) {
		LOG.debug("AbstractTaskEventHander.process() - eventType {}, {}.",
				baseEvent.getClass().getSimpleName(), baseEvent);

		Task task = createTask(baseEvent);
		if (task != null) {
			try {
				taskManager.execute(task);
			} catch (Exception e) {
				LOG.error("Failed to execute task in event handler.", e);
				persistTask(task, e);
			}
		}
	}

	protected abstract Task createTask(AbstractBaseEvent baseEvent);

	private void persistTask(Task task, Exception e) {
		Date now = new Date();
		task.setTimestamp(now.getTime());
		task.setNextTime(now);
		try {
			taskManager.getTaskDao().insertTask(task);
		} catch (SQLException e1) {
			LOG.error("insert task to DB error:{}.", e1);
		}
		
		// sending alert
		String content = String.format("taskContent:%s, taskType:%S.",
				task.getContentStr(), task.getType().getDescription());
		LOG.info("create task and send alert. content: {}", content);

		if (task.getType().getErrorAlert().getValue()) {
			AlertEvent emailEvent = new AlertEvent();
			emailEvent.setSubject(task.getType().getDescription() + "失败");
			emailEvent.setContent(content);
			emailEvent.setException(e);
			emailEvent.setTemplateCode("task.fail");
			emailEvent.setParams(Arrays.asList(task.getType().getDescription()));
			EventManager.send(emailEvent);
		}
	}
	
	@Override
	public int sequence() {
		return DEFAULT_SEQUENCE;
	}
	
	public TaskType getTaskType(Integer id) {
		return taskManager.getTaskType(id);
	}
}
