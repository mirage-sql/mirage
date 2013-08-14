package jp.sf.amateras.mirage.dialect;

import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;

// TODO stored procedure / function and sequence support
public class MySQLDialect extends StandardDialect {

	@Override
    public String getName() {
        return "mysql";
    }

	@Override
	public String getCountSql(String sql) {
		return "SELECT COUNT(*) FROM (" + sql + ") A";
	}

	@Override
	public boolean supportsGenerationType(GenerationType generationType) {
		if(generationType == GenerationType.SEQUENCE){
			return false;
		}
		return true;
	}

}
