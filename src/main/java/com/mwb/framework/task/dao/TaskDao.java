package com.mwb.framework.task.dao;

import com.alibaba.fastjson.JSON;
import com.mwb.framework.log.Log;
import com.mwb.framework.model.Bool;
import com.mwb.framework.model.task.AbstractTaskContent;
import com.mwb.framework.model.task.Task;
import com.mwb.framework.model.task.TaskFailHistory;
import com.mwb.framework.model.task.TaskType;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskDao {
	private static final Log LOG = Log.getLog(TaskDao.class);
	
	private static final String SQL_SELECT_ALL_TASKS_FOR_EXECUTION = 
			"SELECT"
			+" ct.id AS task_id,"
			+" ct.content AS task_content,"
			+" ct.failure_count AS task_failure_count,"
			+" ct.timestamp AS task_timestamp,"
			+" ct.next_time AS task_next_time,"
			+" ct.end_time AS task_end_time,"
			+" ct.is_in_progress AS task_in_progress,"
			+" ct.type_id AS task_type "
			+"FROM  t_task ct "
			+"WHERE"
			+" is_in_progress = 'N' AND next_time <= ?";
	
	
	private static final String SQL_UPDATE_TASK_INPROGRESS = 
			"UPDATE t_task  " 
			+"SET"
			+"  is_in_progress = 'Y', timestamp = ? "
			+"WHERE"
			+"  id = ? AND is_in_progress = 'N' AND timestamp = ?";
	
	private static final String SQL_INSERT_TASK = 
			"INSERT INTO t_task (content, failure_count, is_in_progress, timestamp, next_time, end_time, type_id)" 
			 +"  VALUES (?, 0, 'N', ?, ?, ?, ?)";
	
	private static final String SQL_DELETE_TASK_BY_ID = "DELETE FROM t_task WHERE id = ?";
	
	private static final String SQL_UPDATE_TASK_FAILCOUNT = 
			"UPDATE t_task "
			+"SET"
			+"  failure_count = failure_count + 1,is_in_progress = 'N',`timestamp` = ?,next_time = ?  "
			+"WHERE"
			+"  id = ? AND `timestamp` = ?";
	private static final String SQL_INSERT_FAIL_HISTORY_TASK = 
			"INSERT INTO t_task_fail_history ("
			+"  `content`,"
			+"  `failure_count`,"
			+"  `create_time`,"
			+"  `end_time`,"
			+"  `error_log`,"
			+"  `type_id`"
			+") VALUES (?, ?, ?, ?, ?, ?)";
	
	private static final String SQL_DELETE_FAIL_HISTORY_TASK_BY_CREATETIME = 
			"DELETE FROM t_task_fail_history WHERE `create_time` <= ?";
	
	private static final String SQL_SELECT_ALL_TASK_TYPE = 
			"SELECT id, code, is_error_alert as alert, description, time_intervals as timeIntervals  from t_task_type";
	
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public List<TaskType> getAllTaskType() throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		List<TaskType> res = new ArrayList<TaskType>();
		
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(SQL_SELECT_ALL_TASK_TYPE);
			rs = statement.executeQuery();
			while (rs.next()) {
				TaskType type = new TaskType();
				type.setCode(rs.getString("code"));
				type.setDescription(rs.getString("description"));
				type.setId(rs.getInt("id"));
				type.setErrorAlert(Bool.fromCode(rs.getString("alert")));
				type.setTimeIntervals(rs.getString("timeIntervals"));
				res.add(type);
			}
			return res;
		} finally {
			closeResource(statement, connection, SQL_SELECT_ALL_TASK_TYPE);
		}
	}
	
	
	private void closeResource (PreparedStatement statement, Connection connection, String sql) {
		try {
			if (statement != null) {
				statement.close();
			}
		
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			LOG.error(sql, e);	
		}
	}
	
	public List<Task> selectAllTaskForExecution(Date currentTime) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		List<Task> res = new ArrayList<Task>();
		
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(SQL_SELECT_ALL_TASKS_FOR_EXECUTION);
			statement.setTimestamp(1, new Timestamp(currentTime.getTime()));
			rs = statement.executeQuery();
			
			while (rs.next()) {
				Task task = new Task();
				task.setContent(getNullableResult(rs, "task_content"));
				task.setEndTime(rs.getTimestamp("task_end_time"));
				task.setFailureCount(rs.getInt("task_failure_count"));
				task.setId(rs.getLong("task_id"));
				task.setInProgress(Bool.fromCode(rs.getString("task_in_progress")));
				task.setNextTime(rs.getTimestamp("task_next_time"));
				task.setTimestamp(rs.getLong("task_timestamp"));
				task.setTypeId(rs.getInt("task_type"));
				res.add(task);
			}
			return res;
		} finally {
			closeResource(statement, connection, SQL_SELECT_ALL_TASKS_FOR_EXECUTION);
		}
	}
	private AbstractTaskContent getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		String content = rs.getString(columnName);
		if (content != null) {
			return (AbstractTaskContent)JSON.parse(content);
		}

		return null;
	}

	public int setTaskInProgress(long id, long oldTimestamp, long newTimestamp) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(SQL_UPDATE_TASK_INPROGRESS);
			statement.setLong(1, newTimestamp);
			statement.setLong(2, id);
			statement.setLong(3, oldTimestamp);
			
			int res = statement.executeUpdate();
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
			return res;
			
		} finally {
			closeResource(statement, connection, SQL_UPDATE_TASK_INPROGRESS);
		}
	}
	
	public void insertTask(Task task) throws SQLException{
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(SQL_INSERT_TASK);
			
			statement.setString(1, task.getContentStr());
			statement.setLong(2, task.getTimestamp());
			statement.setTimestamp(3, task.getNextTime() == null ? null : new Timestamp(task.getNextTime().getTime()));
			statement.setTimestamp(4, task.getEndTime() == null ? null : new Timestamp(task.getEndTime().getTime()));
			statement.setInt(5, task.getType().getId());
			
			statement.executeUpdate();
			
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
			
		} finally {
			closeResource(statement, connection, SQL_INSERT_TASK);
		}
	}
	
	public void deleteTask(long id) throws SQLException{
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(SQL_DELETE_TASK_BY_ID);
			
			statement.setLong(1, id);
			
			statement.executeUpdate();
			
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
			
		} finally {
			closeResource(statement, connection, SQL_DELETE_TASK_BY_ID);
		}
	}
	
	public void updateTaskFailure(long id, Date nextTime, long timestamp, long threshholdTimestamp) throws SQLException{
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(SQL_UPDATE_TASK_FAILCOUNT);
			
			statement.setLong(1, timestamp);
			statement.setTimestamp(2, new Timestamp(nextTime.getTime()));
			statement.setLong(3, id);
			statement.setLong(4, threshholdTimestamp);
			
			statement.executeUpdate();
			
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
			
		} finally {
			closeResource(statement, connection, SQL_UPDATE_TASK_FAILCOUNT);
		}
	}
	
	public void insertTaskFailHistory(TaskFailHistory taskFailHistory) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(SQL_INSERT_FAIL_HISTORY_TASK);
			
			statement.setString(1, taskFailHistory.getContent());
			statement.setLong(2, taskFailHistory.getFailureCount());
			statement.setTimestamp(3, taskFailHistory.getCreateTime() == null ? null : new Timestamp(taskFailHistory.getCreateTime().getTime()));
			statement.setTimestamp(4, taskFailHistory.getEndTime() == null ? null : new Timestamp(taskFailHistory.getEndTime().getTime()));
			statement.setString(5, taskFailHistory.getErrorLog());
			statement.setInt(6, taskFailHistory.getType().getId());
			
			statement.executeUpdate();
			
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
			
		} finally {
			closeResource(statement, connection, SQL_INSERT_FAIL_HISTORY_TASK);
		}
	}
	
	public void deleteTaskFailHistory(Date beforeTime) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(SQL_DELETE_FAIL_HISTORY_TASK_BY_CREATETIME);
			
			statement.setTimestamp(1, new Timestamp(beforeTime.getTime()));
			
			statement.executeUpdate();
			
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
			
		} finally {
			closeResource(statement, connection, SQL_DELETE_FAIL_HISTORY_TASK_BY_CREATETIME);
		}
	}
	
	
}
