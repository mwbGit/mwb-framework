package com.mwb.framework.mybatis.typehandler;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description: 反转字符串处理
 * 
 * @author liyong
 * @created 2014年9月30日 下午1:01:39
 */  
public class ReverseStringTypeHandler extends BaseTypeHandler<String> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i,
			String parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, StringUtils.reverse(parameter));
	}

	@Override
	public String getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return StringUtils.reverse(rs.getString(columnName));
	}

	@Override
	public String getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return StringUtils.reverse(rs.getString(columnIndex));
	}

	@Override
	public String getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return StringUtils.reverse(cs.getString(columnIndex));
	}

}
