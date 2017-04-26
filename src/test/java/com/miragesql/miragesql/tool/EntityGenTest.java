package com.miragesql.miragesql.tool;

import com.miragesql.miragesql.AbstractDatabaseTest;
import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import com.miragesql.miragesql.dialect.StandardDialect;
import com.miragesql.miragesql.naming.DefaultNameConverter;
import com.miragesql.miragesql.util.IOUtil;

public class EntityGenTest extends AbstractDatabaseTest {

	/** line separator of EntityGenTest_testGetEntitySource.txt */
	private static final String STANDARD_LINE_SEPARATOR = "\r\n";

	/** line separator used by {@link EntityGen} */
	private static final String ENVIRONMENT_LINE_SEPARATOR = System.getProperty("line.separator");

	public void testGetEntitySource() throws Exception {
		EntityGen gen = new EntityGen();
		gen.setPackageName("com.miragesql.miragesql.entity");
		gen.setNameConverter(new DefaultNameConverter());
		gen.setGenerationType(GenerationType.SEQUENCE);
		gen.setDialect(new StandardDialect());

		String result = gen.getEntitySource(conn, "BOOK", null, null);

		String expect = new String(IOUtil.readStream(
				EntityGenTest.class.getResourceAsStream("EntityGenTest_testGetEntitySource.txt")),
				"UTF-8").replace(STANDARD_LINE_SEPARATOR, ENVIRONMENT_LINE_SEPARATOR);

		assertEquals(expect, result);
	}

}
