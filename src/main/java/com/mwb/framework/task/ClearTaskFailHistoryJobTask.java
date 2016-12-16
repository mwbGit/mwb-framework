package com.mwb.framework.task;

import com.mwb.framework.log.Log;
import com.mwb.framework.scheduler.AbstractStatefulJobTask;
import com.mwb.framework.task.dao.TaskDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

public class ClearTaskFailHistoryJobTask extends AbstractStatefulJobTask {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = Log.getLog(ClearTaskFailHistoryJobTask.class);
	private int lastTime = 90;
	
	@Autowired
	private TaskDao taskDao;
	
	@Override
	public void run() throws Exception {
		LOG.info("ClearTaskFailHistoryJobTask");
		
		long begin = System.currentTimeMillis();
		try {
			 Calendar canlendar = Calendar.getInstance(); // java.util包
		     canlendar.setTime(new Date(begin));
		     canlendar.add(Calendar.DATE, -lastTime); // 日期减 如果不够减会将月变动
		     taskDao.deleteTaskFailHistory(canlendar.getTime());
		} catch (Exception e) {
			LOG.error("Catch an Exception when clear t_task_fail_history.", e);
		}
		
		long end = System.currentTimeMillis();
		LOG.info("ClearTaskFailHistoryJobTask, consume {} ms", end - begin);
	}
	
	public void setLastTime(int lastTime) {
		this.lastTime = lastTime;
	}
	
}
