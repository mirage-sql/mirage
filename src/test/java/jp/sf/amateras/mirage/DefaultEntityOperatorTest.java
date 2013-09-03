package jp.sf.amateras.mirage;

import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.sf.amateras.mirage.annotation.PrimaryKey;
import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import jp.sf.amateras.mirage.bean.BeanDesc;
import jp.sf.amateras.mirage.dialect.Dialect;
import jp.sf.amateras.mirage.naming.NameConverter;
import jp.sf.amateras.mirage.type.BigDecimalValueType;
import jp.sf.amateras.mirage.type.BooleanPrimitiveValueType;
import jp.sf.amateras.mirage.type.BooleanValueType;
import jp.sf.amateras.mirage.type.ByteArrayValueType;
import jp.sf.amateras.mirage.type.DoublePrimitiveValueType;
import jp.sf.amateras.mirage.type.DoubleValueType;
import jp.sf.amateras.mirage.type.FloatPrimitiveValueType;
import jp.sf.amateras.mirage.type.FloatValueType;
import jp.sf.amateras.mirage.type.IntegerPrimitiveValueType;
import jp.sf.amateras.mirage.type.IntegerValueType;
import jp.sf.amateras.mirage.type.LongPrimitiveValueType;
import jp.sf.amateras.mirage.type.LongValueType;
import jp.sf.amateras.mirage.type.ShortPrimitiveValueType;
import jp.sf.amateras.mirage.type.ShortValueType;
import jp.sf.amateras.mirage.type.SqlDateValueType;
import jp.sf.amateras.mirage.type.StringValueType;
import jp.sf.amateras.mirage.type.TimeValueType;
import jp.sf.amateras.mirage.type.TimestampValueType;
import jp.sf.amateras.mirage.type.UtilDateValueType;
import jp.sf.amateras.mirage.type.ValueType;
import junit.framework.TestCase;


public class DefaultEntityOperatorTest extends TestCase {

	DefaultEntityOperator operator = new DefaultEntityOperator();

	public void testDefaultEntityOperator() throws SQLException {
		ResultSet rs = mock(ResultSet.class);
		ResultSetMetaData meta = mock(ResultSetMetaData.class);
		BeanDesc beanDesc = mock(BeanDesc.class);
		Dialect dialect = mock(Dialect.class);
		NameConverter nc = mock(NameConverter.class);

		// Although UserInfo doesn't have public no-args constructor,
		// DefaultResultEntityCreator can create UserInfo instance
		UserInfo userInfo = operator.createEntity(UserInfo.class, rs, meta, 0,
				beanDesc, dialect, getDefaultValueTypes(), nc);

		assertNotNull(userInfo);
	}
	
	public void testDefaultEntityOperator3() throws SQLException {
		ResultSet rs = mock(ResultSet.class);
		ResultSetMetaData meta = mock(ResultSetMetaData.class);
		BeanDesc beanDesc = mock(BeanDesc.class);
		Dialect dialect = mock(Dialect.class);
		NameConverter nc = mock(NameConverter.class);

		// Although UserInfo doesn't have public no-args constructor,
		// DefaultResultEntityCreator can create UserInfo instance
		UserInfo3 userInfo = operator.createEntity(UserInfo3.class, rs, meta, 0,
				beanDesc, dialect, getDefaultValueTypes(), nc);

		assertNotNull(userInfo);
	}

	private List<ValueType<?>> getDefaultValueTypes(){
		List<ValueType<?>> valueTypes = new ArrayList<ValueType<?>>();
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
		return valueTypes;
	}

	public static class UserInfo {

		@PrimaryKey(generationType=GenerationType.IDENTITY)
		private int userId;

		private String userName;

		public UserInfo(int userId, String userName) {
			this.userId = userId;
			this.userName = userName;
		}

		@SuppressWarnings("unused") // for reflective access
		private UserInfo() {
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		@Override
		public String toString() {
			return userId + " " + userName;
		}
	}


//	public static class UserInfo2 {
//
//		@PrimaryKey(generationType=GenerationType.IDENTITY)
//		private int userId;
//
//		private String userName;
//
//		public UserInfo2(int userId, String userName) {
//			this.userId = userId;
//			this.userName = userName;
//		}
//
//		public int getUserId() {
//			return userId;
//		}
//
//		public void setUserId(int userId) {
//			this.userId = userId;
//		}
//
//		public String getUserName() {
//			return userName;
//		}
//
//		public void setUserName(String userName) {
//			this.userName = userName;
//		}
//
//		@Override
//		public String toString() {
//			return userId + " " + userName;
//		}
//	}

	public static class UserInfo3 {

		@PrimaryKey(generationType=GenerationType.IDENTITY)
		private int userId;

		private String userName;

		public UserInfo3(int userId, String userName) {
			if(userId == 0 || userName == null) {
				throw new NullPointerException();
			}
			this.userId = userId;
			this.userName = userName;
		}

		@SuppressWarnings("unused") // for reflective access
		private UserInfo3() {
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		@Override
		public String toString() {
			return userId + " " + userName;
		}
	}
}
