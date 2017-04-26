package com.miragesql.miragesql.dialect;

import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;

// TODO stored procedure / function and sequence support
public class MySQLDialect extends StandardDialect {

	@Override /**{@inheritDoc}**/
    public String getName() {
        return "mysql";
    }

	@Override /**{@inheritDoc}**/
	public String getCountSql(String sql) {
		return "SELECT COUNT(*) FROM (" + sql + ") A";
	}

	@Override /**{@inheritDoc}**/
	public boolean supportsGenerationType(GenerationType generationType) {
		if(generationType == GenerationType.SEQUENCE){
			return false;
		}
		return true;
	}

}
