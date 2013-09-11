/*
 * Copyright 2011 Daisuke Miyamoto.
 * Created on 2011/10/23
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.sf.amateras.mirage.type.enumerate;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import jp.sf.amateras.mirage.type.ValueType;
import jp.sf.amateras.mirage.util.AnnotationUtils;
import jp.sf.amateras.mirage.annotation.Enumerated;
import jp.sf.amateras.mirage.annotation.Enumerated.EnumType;
import jp.sf.amateras.mirage.bean.PropertyDesc;

/**
 * {@link Enum}型をordinalの {@code int}型としてDBに保存するための {@link ValueType}実装クラス。
 * 
 * @since 1.0
 * @version $Id$
 * @author daisuke
 */
public class EnumOrdinalValueType implements ValueType<Object> {
	
	private static Enum<? extends Object> toEnum(Class<? extends Object> type, int ordinal) {
		Object[] values = type.getEnumConstants();
		if (values.length <= ordinal || ordinal < 0) {
			return null;
		}
		return (Enum<?>) values[ordinal];
	}
	
	public Integer get(Class<? extends Object> type, CallableStatement cs, int index) throws SQLException {
		int value = cs.getInt(index);
		if (cs.wasNull()) {
			return null;
		}
		return value;
	}
	
	public Integer get(Class<? extends Object> type, CallableStatement cs, String parameterName) throws SQLException {
		int value = cs.getInt(parameterName);
		if (cs.wasNull()) {
			return null;
		}
		return value;
	}
	
	public Enum<? extends Object> get(Class<? extends Object> type, ResultSet rs, int columnIndex) throws SQLException {
		int ordinal = rs.getInt(columnIndex);
		if (rs.wasNull()) {
			return null;
		}
		return toEnum(type, ordinal);
	}
	
	public Enum<? extends Object> get(Class<? extends Object> type, ResultSet rs, String columnName) throws SQLException {
		int ordinal = rs.getInt(columnName);
		if (rs.wasNull()) {
			return null;
		}
		return toEnum(type, ordinal);
	}
	
	public Class<? extends Object> getJavaType(int sqlType) {
		return Integer.class;
	}
	
	public boolean isSupport(Class<?> type, PropertyDesc propertyDesc) {
		if (Enum.class.isAssignableFrom(type) == false) {
			return false;
		}
		if(propertyDesc != null) {
			Enumerated property = propertyDesc.getAnnotation(Enumerated.class);
			if(property != null && property.value() == EnumType.ORDINAL) {
				return true;
			}
		}
		Enumerated fieldType = AnnotationUtils.findAnnotation(type, Enumerated.class);
		if (fieldType != null && fieldType.value() == EnumType.ORDINAL) {
			return true;
		}
		return false;
	}
	
	public void registerOutParameter(Class<?> type, CallableStatement cs, int index) throws SQLException {
		cs.registerOutParameter(index, Types.INTEGER);
	}
	
	public void registerOutParameter(Class<?> type, CallableStatement cs, String parameterName) throws SQLException {
		cs.registerOutParameter(parameterName, Types.INTEGER);
	}
	
	public void set(Class<? extends Object> type, PreparedStatement stmt, Object value, int index) throws SQLException {
		if (value == null) {
			stmt.setNull(index, Types.INTEGER);
		} else {
			stmt.setInt(index, ((Enum<?>) value).ordinal());
		}
	}

	public Object getDefaultValue() {
		return null;
	}
}
