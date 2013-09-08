package jp.sf.amateras.mirage.bean;

import jp.sf.amateras.mirage.annotation.Enumerated;
import jp.sf.amateras.mirage.annotation.PrimaryKey;
import jp.sf.amateras.mirage.annotation.Transient;
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
