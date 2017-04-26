package com.miragesql.miragesql.dialect;

import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import com.miragesql.miragesql.type.PostgreResultSetValueType;
import junit.framework.TestCase;

public class PostgreSQLDialectTest extends TestCase {

	public void testGetName() {
		PostgreSQLDialect dialect = new PostgreSQLDialect();
		assertEquals("postgresql", dialect.getName());
	}

	public void testGetValueType() {
		PostgreSQLDialect dialect = new PostgreSQLDialect();
		assertTrue(dialect.getValueType() instanceof PostgreResultSetValueType);
	}

	public void testGetSequenceSql() {
		PostgreSQLDialect dialect = new PostgreSQLDialect();
		String sql = dialect.getSequenceSql("SEQUENCE");
		assertEquals("SELECT NEXTVAL('SEQUENCE')", sql);
	}

	public void testSupportsGenerationType() {
		PostgreSQLDialect dialect = new PostgreSQLDialect();
		assertTrue(dialect.supportsGenerationType(GenerationType.APPLICATION));
		assertFalse(dialect.supportsGenerationType(GenerationType.IDENTITY));
		assertTrue(dialect.supportsGenerationType(GenerationType.SEQUENCE));
	}

}
