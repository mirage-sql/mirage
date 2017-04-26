package com.miragesql.miragesql.dialect;

import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;

// TODO: stored procedure / function and sequence support
public class MySQLDialect extends StandardDialect {

	/**{@inheritDoc}**/
	@Override
    public String getName() {
        return "mysql";
    }

	/**{@inheritDoc}**/
	@Override
	public String getCountSql(String sql) {
		return "SELECT COUNT(*) FROM (" + sql + ") A";
	}

	/**{@inheritDoc}**/
	@Override
	public boolean supportsGenerationType(GenerationType generationType) {
		if(generationType == GenerationType.SEQUENCE){
			return false;
		}
		return true;
	}

}
