package com.miragesql.miragesql.dialect;

import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import com.miragesql.miragesql.type.OracleResultSetValueType;
import com.miragesql.miragesql.type.ValueType;

public class OracleDialect extends StandardDialect {

	private OracleResultSetValueType valueType = new OracleResultSetValueType();

    @Override /**{@inheritDoc}**/
    public String getName() {
        return "oracle";
    }

    @Override /**{@inheritDoc}**/
    public boolean needsParameterForResultSet() {
        return true;
    }

    @Override /**{@inheritDoc}**/
	public ValueType<?> getValueType() {
		return valueType;
	}

	@Override /**{@inheritDoc}**/
	public String getSequenceSql(String sequenceName) {
		return String.format("SELECT %s.NEXTVAL FROM DUAL", sequenceName);
	}

	@Override /**{@inheritDoc}**/
	public boolean supportsGenerationType(GenerationType generationType) {
		if(generationType == GenerationType.IDENTITY){
			return false;
		}
		return true;
	}

}
