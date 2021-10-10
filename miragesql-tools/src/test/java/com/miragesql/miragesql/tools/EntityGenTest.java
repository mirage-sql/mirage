package com.miragesql.miragesql.tools;

import com.miragesql.miragesql.AbstractDatabaseTest;
import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import com.miragesql.miragesql.dialect.StandardDialect;
import com.miragesql.miragesql.naming.DefaultNameConverter;
import com.miragesql.miragesql.util.IOUtil;

public class EntityGenTest extends AbstractDatabaseTest {

	/** line separator of EntityGenTest_testGetXXXEntitySource.txt */
	private static final String STANDARD_LINE_SEPARATOR = "\r\n";

	/** line separator used by {@link EntityGen} */
	private static final String ENVIRONMENT_LINE_SEPARATOR = System.getProperty("line.separator");

	public void testGetJavaEntitySource() throws Exception {
		EntityGen gen = new EntityGen();
		gen.setPackageName("com.miragesql.miragesql.entity");
		gen.setNameConverter(new DefaultNameConverter());
		gen.setGenerationType(GenerationType.SEQUENCE);
		gen.setDialect(new StandardDialect());

		String result = gen.getEntitySource(conn, "BOOK", null, null,"java");

		String expect = new String(IOUtil.readStream(
				EntityGenTest.class.getResourceAsStream("EntityGenTest_testGetJavaEntitySource.txt")),
				"UTF-8").replace(STANDARD_LINE_SEPARATOR, ENVIRONMENT_LINE_SEPARATOR);

		assertEquals(expect, result);
	}

	public void testGetGroovyEntitySource() throws Exception {
		EntityGen gen = new EntityGen();
		gen.setPackageName("com.miragesql.miragesql.entity");
		gen.setNameConverter(new DefaultNameConverter());
		gen.setGenerationType(GenerationType.SEQUENCE);
		gen.setDialect(new StandardDialect());

		String result = gen.getEntitySource(conn, "BOOK", null, null,"groovy");

		String expect = new String(IOUtil.readStream(
				EntityGenTest.class.getResourceAsStream("EntityGenTest_testGetGroovyEntitySource.txt")),
				"UTF-8").replace(STANDARD_LINE_SEPARATOR, ENVIRONMENT_LINE_SEPARATOR);

		assertEquals(expect, result);
	}

	public void testGetXmlEntitySource() throws Exception {
		EntityGen gen = new EntityGen();
		gen.setPackageName("com.miragesql.miragesql.entity");
		gen.setNameConverter(new DefaultNameConverter());
		gen.setGenerationType(GenerationType.SEQUENCE);
		gen.setDialect(new StandardDialect());

		String result = gen.getEntitySource(conn, "BOOK", null, null,"xml");

		String expect = new String(IOUtil.readStream(
				EntityGenTest.class.getResourceAsStream("EntityGenTest_testGetXmlEntitySource.txt")),
				"UTF-8").replace(STANDARD_LINE_SEPARATOR, ENVIRONMENT_LINE_SEPARATOR);

		assertEquals(expect, result);
	}

}
