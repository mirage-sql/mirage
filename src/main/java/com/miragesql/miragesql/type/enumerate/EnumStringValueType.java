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

import com.miragesql.miragesql.type.ValueType;
import com.miragesql.miragesql.util.AnnotationUtils;
import com.miragesql.miragesql.annotation.Enumerated;
import com.miragesql.miragesql.annotation.Enumerated.EnumType;
import com.miragesql.miragesql.bean.PropertyDesc;

/**
 * {@link Enum}型を {@link String}型としてDBに保存するための {@link ValueType}実装クラス。
 * 
 * @since 1.0
 * @version $Id$
 * @author daisuke
 */
public class EnumStringValueType implements ValueType<Object> {
	
	@SuppressWarnings({
		"rawtypes",
		"unchecked"
	})
	private static Enum<? extends Object> toEnum(Class type, String name) {
		try {
			return Enum.valueOf(type, name);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public Enum<? extends Object> get(Class<? extends Object> type, CallableStatement cs, int index) throws SQLException {
		String name = cs.getString(index);
		return name == null ? null : toEnum(type, name);
	}
	
	public Enum<? extends Object> get(Class<? extends Object> type, CallableStatement cs, String parameterName) throws SQLException {
		String name = cs.getString(parameterName);
		return name == null ? null : toEnum(type, name);
	}
	
	public Enum<? extends Object> get(Class<? extends Object> type, ResultSet rs, int columnIndex) throws SQLException {
		String name = rs.getString(columnIndex);
		return name == null ? null : toEnum(type, name);
	}
	
	public Enum<? extends Object> get(Class<? extends Object> type, ResultSet rs, String columnName) throws SQLException {
		String name = rs.getString(columnName);
		return name == null ? null : toEnum(type, name);
	}
	
	public Class<? extends Object> getJavaType(int sqlType) {
		return String.class;
	}
	
	public boolean isSupport(Class<?> type, PropertyDesc propertyDesc) {
		if (Enum.class.isAssignableFrom(type) == false) {
			return false;
		}
		if(propertyDesc != null) {
			Enumerated property = propertyDesc.getAnnotation(Enumerated.class);
			if(property != null && property.value() == EnumType.STRING) {
				return true;
			}
		}
		Enumerated fieldType = AnnotationUtils.findAnnotation(type, Enumerated.class);
		if (fieldType != null && fieldType.value() == EnumType.STRING) {
			return true;
		}
		return false;
	}
	
	public void registerOutParameter(Class<?> type, CallableStatement cs, int index) throws SQLException {
		cs.registerOutParameter(index, Types.VARCHAR);
	}
	
	public void registerOutParameter(Class<?> type, CallableStatement cs, String parameterName) throws SQLException {
		cs.registerOutParameter(parameterName, Types.VARCHAR);
	}
	
	public void set(Class<? extends Object> type, PreparedStatement stmt, Object value, int index) throws SQLException {
		if (value == null) {
			stmt.setNull(index, Types.VARCHAR);
		} else {
			stmt.setString(index, ((Enum<?>) value).name());
		}
	}

	public Object getDefaultValue() {
		return null;
	}
}
