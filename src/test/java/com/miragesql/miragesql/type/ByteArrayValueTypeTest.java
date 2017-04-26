package com.miragesql.miragesql.type;

import junit.framework.TestCase;

public class ByteArrayValueTypeTest extends TestCase {

	public void testIsSupport() {
		ByteArrayValueType valueType = new ByteArrayValueType();

		byte[] bytes = new byte[0];
		Object[] objs = new Object[0];

		assertTrue(valueType.isSupport(bytes.getClass(), null));
		assertFalse(valueType.isSupport(objs.getClass(), null));
	}

}
