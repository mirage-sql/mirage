package jp.sf.amateras.mirage.dialect;

import junit.framework.TestCase;

public class HyperSQLDialectTest extends TestCase {

	public void testGetSequenceSql() {
		HyperSQLDialect dialect = new HyperSQLDialect();
		String sql = dialect.getSequenceSql("SEQUENCE");

		assertEquals("SELECT NEXT VALUE FOR SEQUENCE " +
				"FROM INFORMATION_SCHEMA.SYSTEM_TABLES " +
				"WHERE table_name = 'SYSTEM_TABLES'", sql);
	}

}
