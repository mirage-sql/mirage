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
package com.miragesql.miragesql.type.enumerate;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.miragesql.miragesql.annotation.Enumerated;
import com.miragesql.miragesql.annotation.Enumerated.EnumType;
import com.miragesql.miragesql.bean.PropertyDesc;
import com.miragesql.miragesql.type.ValueType;
import com.miragesql.miragesql.util.AnnotationUtils;

/**
 * {@link Enum}型をordinal+1の {@code int}型としてDBに保存するための {@link ValueType}実装クラス。
 * 
 * @author daisuke
 */
public class EnumOneBasedOrdinalValueType implements ValueType<Object> {
	
	private static Enum<? extends Object> toEnum(Class<? extends Object> type, int ordinal) {
		int o = ordinal - 1;
		Object[] values = type.getEnumConstants();
		if (values.length <= o || o < 0) {
			return null;
		}
		return (Enum<?>) values[o];
	}
	
	public Integer get(Class<? extends Object> type, CallableStatement cs, int index) throws SQLException {
		int value = cs.getInt(index);
		return value;
	}
	
	public Integer get(Class<? extends Object> type, CallableStatement cs, String parameterName) throws SQLException {
		int value = cs.getInt(parameterName);
		return value;
	}
	
	public Enum<? extends Object> get(Class<? extends Object> type, ResultSet rs, int columnIndex) throws SQLException {
		int ordinal = rs.getInt(columnIndex);
		return toEnum(type, ordinal);
	}
	
	public Enum<? extends Object> get(Class<? extends Object> type, ResultSet rs, String columnName) throws SQLException {
		int ordinal = rs.getInt(columnName);
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
			if(property != null && property.value() == EnumType.ONE_BASED_ORDINAL) {
				return true;
			}
		}
		Enumerated fieldType = AnnotationUtils.findAnnotation(type, Enumerated.class);
		if (fieldType != null && fieldType.value() == EnumType.ONE_BASED_ORDINAL) {
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
			stmt.setInt(index, ((Enum<?>) value).ordinal() + 1);
		}
	}

	public Object getDefaultValue() {
		return null;
	}
}
