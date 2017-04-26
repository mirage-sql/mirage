package com.miragesql.miragesql.bean;

import com.miragesql.miragesql.annotation.Enumerated;
import com.miragesql.miragesql.annotation.PrimaryKey;
import com.miragesql.miragesql.annotation.Transient;
import junit.framework.TestCase;

public class BeanDescImplTest extends TestCase {

	public void testBeanDescImpl() {
		PropertyExtractor propertyExtractor = new DefaultPropertyExtractor();
		BeanDesc bd = new BeanDescImpl(Book.class, propertyExtractor.extractProperties(Book.class));

		assertEquals(3, bd.getPropertyDescSize());
		assertNotNull(bd.getPropertyDesc("bookId").getAnnotation(PrimaryKey.class));
		assertNotNull(bd.getPropertyDesc("bookName").getAnnotation(Transient.class));
		assertNotNull(bd.getPropertyDesc("bookType").getAnnotation(Enumerated.class));
	}

}
