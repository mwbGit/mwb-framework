package com.mwb.framework.mybatis.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EnumIdTypeHandler<E extends Enum<? extends IdInterface>> extends BaseTypeHandler<E> {

	private Class<E> type;
	private final E[] enums;
	private Map<Integer, E> enumsMap;

	public EnumIdTypeHandler(Class<E> type) {
		this.type = type;
		this.enums = type.getEnumConstants();
		if (this.enums == null) {
			throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
		}

		enumsMap = new HashMap<Integer, E>();
		for (E e : enums) {
			enumsMap.put(((IdInterface) e).getId(), e);
		}
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		ps.setInt(i, parameter.ordinal());
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		int i = rs.getInt(columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			try {
				return enumsMap.get(i);
			} catch (Exception ex) {
				throw new IllegalArgumentException("Cannot convert " + i + " to " + type.getSimpleName() + " by ordinal value.", ex);
			}
		}
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		int i = rs.getInt(columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			try {
				return enumsMap.get(i);
			} catch (Exception ex) {
				throw new IllegalArgumentException("Cannot convert " + i + " to " + type.getSimpleName() + " by ordinal value.", ex);
			}
		}
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		int i = cs.getInt(columnIndex);
		if (cs.wasNull()) {
			return null;
		} else {
			try {
				return enumsMap.get(i);
			} catch (Exception ex) {
				throw new IllegalArgumentException("Cannot convert " + i + " to " + type.getSimpleName() + " by ordinal value.", ex);
			}
		}
	}

}
