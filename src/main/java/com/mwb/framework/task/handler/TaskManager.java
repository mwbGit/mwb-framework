package com.mwb.framework.task.handler;

import com.mwb.framework.log.Log;
import com.mwb.framework.model.task.Task;
import com.mwb.framework.model.task.TaskType;
import com.mwb.framework.task.dao.TaskDao;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager implements BeanPostProcessor {
	private static final Log LOG = Log.getLog(TaskManager.class);
	
	private Map<String, ITaskEventHandler> taskHandlers = new HashMap<String, ITaskEventHandler>();
	
	private static Map<Integer, TaskType> taskTypeMap = new HashMap<Integer, TaskType>();
	
	@Autowired
	private TaskDao taskDao;
	
	
	public void init() throws SQLException {
		List<TaskType> res = taskDao.getAllTaskType();
		for (TaskType taskType : res) {
			taskTypeMap.put(taskType.getId(), taskType);
		}
	}
	
	public void execute(Task task) throws Exception {
		LOG.debug("Starting task {}.", task);
		
		TaskType type = task.getType();
		
		ITaskEventHandler taskHandler = taskHandlers.get(type.getCode());
		
		taskHandler.handle(task);
		
		LOG.debug("Finished task {}.", task);
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof ITaskEventHandler) {
			taskHandlers.put(((ITaskEventHandler) bean).getTaskType(), (ITaskEventHandler) bean);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

    public List<Task> selectAllTaskForExecution(Date currentTime) throws SQLException {
    	List<Task> res = taskDao.selectAllTaskForExecution(currentTime);
    	for (Task task : res) {
    		task.setType(taskTypeMap.get(task.getTypeId()));
		}
    	return res;
	}
    
    public TaskType getTaskType(Integer id) {
    	return taskTypeMap.get(id);
    }
	public TaskDao getTaskDao() {
		return taskDao;
	}

}

