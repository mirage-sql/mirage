package jp.sf.amateras.mirage.dialect;

import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import jp.sf.amateras.mirage.type.ValueType;

public class StandardDialect implements Dialect {

	public String getName() {
		return null;
	}

	public boolean needsParameterForResultSet() {
		return false;
	}

	public ValueType<?> getValueType() {
		return null;
	}

	public String getSequenceSql(String sequenceName) {
		return null;
	}

	public boolean supportsGenerationType(GenerationType generationType) {
		return true;
	}

	public String getCountSql(String sql) {
		return "SELECT COUNT(*) FROM (" + sql + ")";
	}

}
