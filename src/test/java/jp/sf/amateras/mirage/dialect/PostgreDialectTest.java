package jp.sf.amateras.mirage.dialect;

import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import jp.sf.amateras.mirage.type.PostgreResultSetValueType;
import junit.framework.TestCase;

public class PostgreDialectTest extends TestCase {

	public void testGetName() {
		PostgreDialect dialect = new PostgreDialect();
		assertEquals("postgre", dialect.getName());
	}

	public void testGetValueType() {
		PostgreDialect dialect = new PostgreDialect();
		assertTrue(dialect.getValueType() instanceof PostgreResultSetValueType);
	}

	public void testGetSequenceSql() {
		PostgreDialect dialect = new PostgreDialect();
		String sql = dialect.getSequenceSql("SEQUENCE");
		assertEquals("SELECT NEXTVAL('SEQUENCE')", sql);
	}

	public void testSupportsGenerationType() {
		PostgreDialect dialect = new PostgreDialect();
		assertTrue(dialect.supportsGenerationType(GenerationType.APPLICATION));
		assertFalse(dialect.supportsGenerationType(GenerationType.IDENTITY));
		assertTrue(dialect.supportsGenerationType(GenerationType.SEQUENCE));
	}

}
