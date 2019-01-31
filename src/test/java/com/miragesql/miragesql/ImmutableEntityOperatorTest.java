package com.miragesql.miragesql;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.miragesql.miragesql.annotation.Column;
import com.miragesql.miragesql.annotation.PrimaryKey;
import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import com.miragesql.miragesql.bean.BeanDesc;
import com.miragesql.miragesql.dialect.Dialect;
import com.miragesql.miragesql.naming.NameConverter;
import com.miragesql.miragesql.type.BigDecimalValueType;
import com.miragesql.miragesql.type.BooleanPrimitiveValueType;
import com.miragesql.miragesql.type.BooleanValueType;
import com.miragesql.miragesql.type.ByteArrayValueType;
import com.miragesql.miragesql.type.DoublePrimitiveValueType;
import com.miragesql.miragesql.type.DoubleValueType;
import com.miragesql.miragesql.type.FloatPrimitiveValueType;
import com.miragesql.miragesql.type.FloatValueType;
import com.miragesql.miragesql.type.IntegerPrimitiveValueType;
import com.miragesql.miragesql.type.IntegerValueType;
import com.miragesql.miragesql.type.LongPrimitiveValueType;
import com.miragesql.miragesql.type.LongValueType;
import com.miragesql.miragesql.type.ShortPrimitiveValueType;
import com.miragesql.miragesql.type.ShortValueType;
import com.miragesql.miragesql.type.SqlDateValueType;
import com.miragesql.miragesql.type.StringValueType;
import com.miragesql.miragesql.type.TimeValueType;
import com.miragesql.miragesql.type.TimestampValueType;
import com.miragesql.miragesql.type.UtilDateValueType;
import com.miragesql.miragesql.type.ValueType;
import com.miragesql.miragesql.type.enumerate.EnumOneBasedOrdinalValueType;
import com.miragesql.miragesql.type.enumerate.EnumOrdinalValueType;
import com.miragesql.miragesql.type.enumerate.EnumStringValueType;

import junit.framework.TestCase;

public class ImmutableEntityOperatorTest extends TestCase {
	
	ImmutableEntityOperator operator = new ImmutableEntityOperator();

	public void testImmutableEntityOperator() throws SQLException {
		ResultSet rs = mock(ResultSet.class);
		ResultSetMetaData meta = mock(ResultSetMetaData.class);
		BeanDesc beanDesc = mock(BeanDesc.class);
		Dialect dialect = mock(Dialect.class);
		NameConverter nc = mock(NameConverter.class);
		
		when(rs.getInt(eq("userId"))).thenReturn(1);
		when(rs.getString("userName")).thenReturn("userName");
		
		
		UserInfo userInfo = operator.createEntity(UserInfo.class, rs, meta, 0,
				beanDesc, dialect, getDefaultValueTypes(), nc);

		assertEquals( 1, userInfo.userId);
		assertEquals("userName", userInfo.userName);
	}

	private List<ValueType<?>> getDefaultValueTypes(){
		List<ValueType<?>> valueTypes = new ArrayList<>();
		valueTypes.add(new StringValueType());
		valueTypes.add(new IntegerValueType());
		valueTypes.add(new IntegerPrimitiveValueType());
		valueTypes.add(new LongValueType());
		valueTypes.add(new LongPrimitiveValueType());
		valueTypes.add(new ShortValueType());
		valueTypes.add(new ShortPrimitiveValueType());
		valueTypes.add(new DoubleValueType());
		valueTypes.add(new DoublePrimitiveValueType());
		valueTypes.add(new FloatValueType());
		valueTypes.add(new FloatPrimitiveValueType());
		valueTypes.add(new BooleanValueType());
		valueTypes.add(new BooleanPrimitiveValueType());
		valueTypes.add(new BigDecimalValueType());
		valueTypes.add(new SqlDateValueType());
		valueTypes.add(new UtilDateValueType());
		valueTypes.add(new TimeValueType());
		valueTypes.add(new TimestampValueType());
		valueTypes.add(new ByteArrayValueType());
		valueTypes.add(new EnumStringValueType());
		valueTypes.add(new EnumOrdinalValueType());
		valueTypes.add(new EnumOneBasedOrdinalValueType());
		return valueTypes;
	}

	public static class UserInfo {

		@PrimaryKey(generationType=GenerationType.IDENTITY)
		public final int userId;
		
		public final String userName;

		public UserInfo(@Column(name = "userId") int userId, @Column(name = "userName") String userName) {
			this.userId = userId;
			this.userName = userName;
		}

		@Override
		public String toString() {
			return userId + " " + userName;
		}
	}

}
