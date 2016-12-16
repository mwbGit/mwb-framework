package com.mwb.framework.scheduler.dao;

import com.mwb.framework.model.Bool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JobTaskExecutionStatusDao {
	private DataSource dataSource;
	
	private String jobTaskExecutionStatusTable;
	
	private String jobTaskExecutionStatusColumns;
	
	private String insertOrUpdateJobTaskExecutionStatusSQL;
	private String selectJobTaskExecutionStatusByNameAndGroupNameSQL;
	private String selectAllJobTaskExecutionStatusSQL;
	
	public JobTaskExecutionStatusDao(String tSchedulerExecutionLogTbl) throws Exception {	
		jobTaskExecutionStatusTable = tSchedulerExecutionLogTbl;
		
		jobTaskExecutionStatusColumns = "cst.id AS scheduler_task_id,"
								+ "cst.alias AS scheduler_task_alias,"
								+ "cst.cron_expression AS scheduler_task_cron_expression,"
								+ "cst.class_name AS scheduler_task_class_name,"
								+ "cst.description AS scheduler_task_description,"
								+ "cst.is_manual_supported AS scheduler_task_manual_supported,"
								+ "cst.is_execute_success AS scheduler_task_execute_success,"
								+ "cst.execute_duration AS scheduler_task_execute_duration,"
								+ "cst.execute_time AS scheduler_task_execute_time,"
								+ "cst.group_name AS scheduler_task_group_name,"
								+ "cst.name AS scheduler_task_name";
		
		insertOrUpdateJobTaskExecutionStatusSQL = "INSERT INTO " + jobTaskExecutionStatusTable + " ("
				        	+ "id,"
				        	+ "name,"
							+ "alias,"
							+ "group_name,"
							+ "cron_expression,"
							+ "class_name,"
							+ "description,"
							+ "is_manual_supported,"
							+ "is_execute_success,"
							+ "execute_duration,"
							+ "execute_time"
						+ ") VALUES ("
							+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
							+ ") ON DUPLICATE KEY UPDATE "
				        	+ "alias = ?, "
				        	+ "cron_expression = ?, "
				        	+ "is_manual_supported = ?, "
				        	+ "is_execute_success = ? ,"
				        	+ "execute_duration = ? ,"
				        	+ "execute_time = ?";
		
		selectJobTaskExecutionStatusByNameAndGroupNameSQL = "SELECT "
													+ jobTaskExecutionStatusColumns
													+ " FROM "
													+ jobTaskExecutionStatusTable
													+ " AS cst WHERE "
													+ "cst.name = ? "
													+ "AND cst.group_name = ?";
		
		selectAllJobTaskExecutionStatusSQL = "SELECT "	
									+ jobTaskExecutionStatusColumns
									+ " FROM "
									+ jobTaskExecutionStatusTable
									+ " cst  "
									+ "ORDER BY cst.execute_time DESC";
		
	}
	
	public void insertOrUpdateJobTaskExecutionStatus(JobTaskExecutionStatus task)  throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(insertOrUpdateJobTaskExecutionStatusSQL);
			
			statement.setInt(1, task.getId());
			statement.setString(2, task.getName());
			statement.setString(3, task.getAlias());
			statement.setString(4, task.getGroupName());
			statement.setString(5, task.getCronExpression());
			statement.setString(6, task.getClassName());
			statement.setString(7, task.getDescription());
			statement.setString(8, task.getManualSupported().getCode());
			statement.setString(9, task.getIsLastExecutionSucess().getCode());
			statement.setInt(10, task.getLastExecutionDuration());
			statement.setTimestamp(11, new java.sql.Timestamp(task.getExecuteTime().getTime()) );
			statement.setString(12, task.getAlias());
			statement.setString(13, task.getCronExpression());
			statement.setString(14, task.getManualSupported().getCode());
			statement.setString(15, task.getIsLastExecutionSucess().getCode());
			statement.setInt(16, task.getLastExecutionDuration());
			statement.setTimestamp(17, new java.sql.Timestamp(task.getExecuteTime().getTime()) );
			
			statement.executeUpdate();
			
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
					
			}
		}
	}
	
	public JobTaskExecutionStatus selectJobTaskExecutionStatusByNameAndGroupName(String name, String groupName)  throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(selectJobTaskExecutionStatusByNameAndGroupNameSQL);
			
			statement.setString(1, name);
			statement.setString(2, groupName);
			
			rs = statement.executeQuery();
			
			JobTaskExecutionStatus st = null;
			
			while (rs.next()) {
				st = new JobTaskExecutionStatus();
				st.setId(rs.getInt("scheduler_task_id"));
				st.setAlias(rs.getString("scheduler_task_alias"));
				st.setClassName(rs.getString("scheduler_task_class_name"));
				st.setCronExpression(rs.getString("scheduler_task_cron_expression"));
				st.setDescription(rs.getString("scheduler_task_description"));
				st.setLastExecutionDuration(rs.getInt("scheduler_task_execute_duration"));
				st.setIsLastExecutionSucess(Bool.fromCode(rs.getString("scheduler_task_execute_success")));
				st.setExecuteTime(rs.getTimestamp("scheduler_task_execute_time"));
				st.setManualSupported(Bool.fromCode(rs.getString("scheduler_task_manual_supported")));
				st.setName(rs.getString("scheduler_task_name"));
				st.setGroupName(rs.getString("scheduler_task_group_name"));
			}
			
			return st;
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
					
			}
		}
	}
	
	public List<JobTaskExecutionStatus> selectAllJobTaskExecutionStatus() throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		List<JobTaskExecutionStatus> schedulerTasks = new ArrayList<JobTaskExecutionStatus>();
		
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(selectAllJobTaskExecutionStatusSQL);
			
			rs = statement.executeQuery();
			
			while (rs.next()) {
				JobTaskExecutionStatus st = new JobTaskExecutionStatus();

				st.setId(rs.getInt("scheduler_task_id"));
				st.setAlias(rs.getString("scheduler_task_alias"));
				st.setClassName(rs.getString("scheduler_task_class_name"));
				st.setCronExpression(rs.getString("scheduler_task_cron_expression"));
				st.setDescription(rs.getString("scheduler_task_description"));
				st.setLastExecutionDuration(rs.getInt("scheduler_task_execute_duration"));
				st.setIsLastExecutionSucess(Bool.fromCode(rs.getString("scheduler_task_execute_success")));
				st.setExecuteTime(rs.getTimestamp("scheduler_task_execute_time"));
				st.setGroupName(rs.getString("scheduler_task_group_name"));
				st.setManualSupported(Bool.fromCode(rs.getString("scheduler_task_manual_supported")));
				st.setName(rs.getString("scheduler_task_name"));
				
				schedulerTasks.add(st);
			}
			
			return schedulerTasks;
			
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
					
			}
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setJobTaskExecutionStatusTable(String jobTaskExecutionStatusTable) {
		this.jobTaskExecutionStatusTable = jobTaskExecutionStatusTable;
	}


}
