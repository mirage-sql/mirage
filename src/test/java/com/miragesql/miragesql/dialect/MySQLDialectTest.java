package jp.sf.amateras.mirage.dialect;

import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import junit.framework.TestCase;

public class MySQLDialectTest extends TestCase {

	public void testGetName() {
		MySQLDialect dialect = new MySQLDialect();
		assertEquals("mysql", dialect.getName());
	}

	public void testSupportsGenerationType() {
		MySQLDialect dialect = new MySQLDialect();
		assertTrue(dialect.supportsGenerationType(GenerationType.APPLICATION));
		assertTrue(dialect.supportsGenerationType(GenerationType.IDENTITY));
		assertFalse(dialect.supportsGenerationType(GenerationType.SEQUENCE));
	}

	public void testGetCountSql() {
		MySQLDialect dialect = new MySQLDialect();
		String sql = dialect.getCountSql("SELECT * FROM TABLE");
		assertEquals("SELECT COUNT(*) FROM (SELECT * FROM TABLE) A", sql);
	}

}
