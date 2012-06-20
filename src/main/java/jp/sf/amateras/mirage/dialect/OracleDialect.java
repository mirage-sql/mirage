package jp.sf.amateras.mirage.dialect;

import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import jp.sf.amateras.mirage.type.OracleResultSetValueType;
import jp.sf.amateras.mirage.type.ValueType;

public class OracleDialect extends StandardDialect {

	private OracleResultSetValueType valueType = new OracleResultSetValueType();

    @Override
    public String getName() {
        return "oracle";
    }

    @Override
    public boolean needsParameterForResultSet() {
        return true;
    }

    @Override
	public ValueType<?> getValueType() {
		return valueType;
	}

	@Override
	public String getSequenceSql(String sequenceName) {
		return String.format("SELECT %s.NEXTVAL FROM DUAL", sequenceName);
	}

	@Override
	public boolean supportsGenerationType(GenerationType generationType) {
		if(generationType == GenerationType.IDENTITY){
			return false;
		}
		return true;
	}

}
