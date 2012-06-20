package jp.sf.amateras.mirage.parser;

import junit.framework.TestCase;

public class SqlParserImplTest extends TestCase {

	public void testParse() {
		SqlParser parser = new SqlParserImpl(
				"SELECT * FROM USER /*IF user.type == 0*/WHERE USER_ID=/*user.userId*//*END*/");
		Node node = parser.parse();

		UserDto user = new UserDto();
		SqlContext context = new SqlContextImpl();
		context.addArg("user", user, UserDto.class);

		node.accept(context);

		assertEquals("SELECT * FROM USER WHERE USER_ID=?", context.getSql());
		assertEquals(1, context.getBindVariables().length);
		assertEquals("takezoe", context.getBindVariables()[0]);
	}

	public static class UserDto {
		public int type = 0;
		public String userId = "takezoe";
	}

}
