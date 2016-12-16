package com.mwb.framework.gid;

import com.hazelcast.core.ILock;
import com.mwb.framework.hazelcast.AbstractHazelcast;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 使用单独的dataSource，避免受到外部transaction的影响
public abstract class AbstractGlobalIdProductor extends AbstractHazelcast {
	
	private ILock idLock;
	
	private long currentId;
	private long thresholdId;

	private DataSource dataSource;
	
	private String idTable;
	private String idColumn;
	private String nameColumn;
	private String valueColumn;
	
	private String querySQL;
	private String updateSQL;
	
	public synchronized void init() throws Exception {		
		querySQL = "SELECT " + idColumn + ", " + valueColumn + " FROM " + idTable + " WHERE " + nameColumn + " = ?";
		updateSQL = "UPDATE " + idTable + " SET " + valueColumn + " = ? WHERE " + idColumn + " = ?";

	    idLock = getHazelcast().getLock(getHolderName("IdLock"));
        
	    currentId = -1;
		thresholdId = -1;
	}
	
	public synchronized String newId() throws Exception {
		
		if (currentId < thresholdId) { 
			
			return generateId(currentId++);
			
		} else { // 第一次调用或者预先获取的id已用尽
			
			fetchReservedNumberId();
			
			return generateId(currentId++);
		}
	}
	
	private int fetchReservedNumberId() throws Exception {
		idLock.lock();
		try {
			GlobalId globalId = getGlobalIdByName(getIdName());
			
			if (globalId == null) {
				throw new Exception("Cannot get global ID from database!");
			}
			
			long globalIdValue = globalId.getValue();
			
			globalId.setValue(globalIdValue + getReservedNumber());
			updateGlobalId(globalId);
			
			currentId = globalIdValue;
			thresholdId = currentId + getReservedNumber();
		} finally {
			idLock.unlock();
		}
		
		return getReservedNumber();
	}
	
	private GlobalId getGlobalIdByName(String idName) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(querySQL);
			
			statement.setString(1, idName);
			
			rs = statement.executeQuery();
			
			List<GlobalId> globalIds = new ArrayList<GlobalId>();
			
			while (rs.next()) {
				int id = rs.getInt(idColumn);
				long value = rs.getLong(valueColumn);
				
				GlobalId globalId = new GlobalId();
				
				globalId.setId(id);
				globalId.setName(idName);
				globalId.setValue(value);
				
				globalIds.add(globalId);
			}
			
			if (globalIds.size() == 1) {
				return globalIds.get(0);
			} else {
				return null;
			}
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

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
	
	private void updateGlobalId(GlobalId globalId) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(updateSQL);
			
			statement.setLong(1, globalId.getValue());
			statement.setInt(2, globalId.getId());
			
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
	
	protected abstract String generateId(long id);
	protected abstract String getIdName();
	protected abstract int getReservedNumber();

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setIdTable(String idTable) {
		this.idTable = idTable;
	}
	
	public void setIdColumn(String idColumn) {
		this.idColumn = idColumn;
	}

	public void setNameColumn(String nameColumn) {
		this.nameColumn = nameColumn;
	}

	public void setValueColumn(String valueColumn) {
		this.valueColumn = valueColumn;
	}
}
