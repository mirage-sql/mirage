package jp.sf.amateras.mirage.dialect;

public class HyperSQLDialect extends StandardDialect {

	@Override
	public String getName() {
		return "hsqldb";
	}
	
	@Override
	public String getSequenceSql(String sequenceName) {
		return String.format("SELECT NEXT VALUE FOR %s " +
				"FROM INFORMATION_SCHEMA.SYSTEM_TABLES " +
				"WHERE table_name = 'SYSTEM_TABLES'", sequenceName);
	}

}
