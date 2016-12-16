package com.mwb.framework.scheduler.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QZJobDetailDao {
	private DataSource dataSource;
	
	private String qzJobDetailTable;
	
	private String quartzJobAllColumns;
	
	private String selectAllQuartzTaskSQL;
	
	public QZJobDetailDao(String qzJobDetailTbl) {
		qzJobDetailTable = qzJobDetailTbl;
		
		quartzJobAllColumns = "job_name,"
								+ "job_group,"
								+ "job_class_name,"
								+ "description ";
		
		selectAllQuartzTaskSQL = "SELECT "	
									+ quartzJobAllColumns
									+ "FROM "
									+ qzJobDetailTable
									+ " ORDER BY job_name ASC";
	}
	
	public List<QZJobDetail> selectAllQuartzJobTask() throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		List<QZJobDetail> details = new ArrayList<QZJobDetail>();
		
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement(selectAllQuartzTaskSQL);
			
			rs = statement.executeQuery();
			
			while (rs.next()) {
				QZJobDetail st = new QZJobDetail();

				st.setJobClassName(rs.getString("job_class_name"));
				st.setDescription(rs.getString("description"));
				st.setJobGroup(rs.getString("job_group"));
				st.setJobName(rs.getString("job_name"));
				
				details.add(st);
			}
			
			return details;
			
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

	public void setQzJobDetailTable(String qzJobDetailTable) {
		this.qzJobDetailTable = qzJobDetailTable;
	}
}
