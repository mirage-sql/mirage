package com.miragesql.miragesql.dialect;

import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import junit.framework.TestCase;

public class StandardDialectTest extends TestCase {

	public void testGetName() {
		StandardDialect dialect = new StandardDialect();
		assertNull(dialect.getName());
	}

	public void testNeedsParameterForResultSet() {
		StandardDialect dialect = new StandardDialect();
		assertFalse(dialect.needsParameterForResultSet());
	}

	public void testGetValueType() {
		StandardDialect dialect = new StandardDialect();
		assertNull(dialect.getValueType());
	}

	public void testGetSequenceSql() {
		StandardDialect dialect = new StandardDialect();
		assertNull(dialect.getSequenceSql("SEQUENCE"));
	}

	public void testSupportsGenerationType() {
		StandardDialect dialect = new StandardDialect();
		assertTrue(dialect.supportsGenerationType(GenerationType.APPLICATION));
		assertTrue(dialect.supportsGenerationType(GenerationType.IDENTITY));
		assertTrue(dialect.supportsGenerationType(GenerationType.SEQUENCE));
	}

	public void testGetCountSql() {
		StandardDialect dialect = new StandardDialect();
		String sql = dialect.getCountSql("SELECT * FROM TABLE");
		assertEquals("SELECT COUNT(*) FROM (SELECT * FROM TABLE)", sql);
	}

}
