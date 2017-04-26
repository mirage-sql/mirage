package jp.sf.amateras.mirage.tool;

import jp.sf.amateras.mirage.AbstractDatabaseTest;
import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import jp.sf.amateras.mirage.dialect.StandardDialect;
import jp.sf.amateras.mirage.naming.DefaultNameConverter;
import jp.sf.amateras.mirage.util.IOUtil;

public class EntityGenTest extends AbstractDatabaseTest {

	/** line separator of EntityGenTest_testGetEntitySource.txt */
	private static final String STANDARD_LINE_SEPARATOR = "\r\n";

	/** line separator used by {@link EntityGen} */
	private static final String ENVIRONMENT_LINE_SEPARATOR = System.getProperty("line.separator");

	public void testGetEntitySource() throws Exception {
		EntityGen gen = new EntityGen();
		gen.setPackageName("jp.sf.amateras.mirage.entity");
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
