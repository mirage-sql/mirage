package com.miragesql.miragesql.naming;

import junit.framework.TestCase;

public class DefaultNameConverterTest extends TestCase {

	public void testColumnToProperty() {
		NameConverter converter = new DefaultNameConverter();

		assertEquals("userId", converter.columnToProperty("USER_ID"));
	}

	public void testEntityToTable() {
		NameConverter converter = new DefaultNameConverter();

		assertEquals("USER_INFO", converter.entityToTable("UserInfo"));
		assertEquals("USER_INFO", converter.entityToTable("entity.UserInfo"));
	}

	public void testPropertyToColumn() {
		NameConverter converter = new DefaultNameConverter();

		assertEquals("USER_ID", converter.propertyToColumn("userId"));
	}

}
