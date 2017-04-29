package com.miragesql.miragesql.integration.spring;

import java.lang.reflect.Field;

import com.miragesql.miragesql.AbstractDatabaseTest;
import com.miragesql.miragesql.SqlManager;
import com.miragesql.miragesql.SqlManagerImpl;
import com.miragesql.miragesql.SqlManagerImplTest.Book;
import com.miragesql.miragesql.dialect.Dialect;
import com.miragesql.miragesql.dialect.HyperSQLDialect;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringConnectionProviderTest extends AbstractDatabaseTest {

	private static final String APPLICATION_CONTEXT =
		"com/miragesql/miragesql/integration/spring/applicationContext.xml";

	/**
	 * Tests dialect configuration.
	 */
	public void testSpringSpringConnectionProvider1() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT);

		SqlManager sqlManager = (SqlManager) applicationContext.getBean("sqlManager");

		Field field = SqlManagerImpl.class.getDeclaredField("dialect");
		field.setAccessible(true);
		Dialect dialect = (Dialect) field.get(sqlManager);

		assertTrue(dialect instanceof HyperSQLDialect);
	}

	/**
	 * Tests SQL execution (commit).
	 */
	public void testSpringSpringConnectionProvider2() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT);

		SpringTestDao springTestDao = (SpringTestDao) applicationContext.getBean("springTestDao");

		Book book = new Book();
		book.name = "Mirage in Action";
		book.author = "Naoki Takezoe";
		book.price = 20;
		springTestDao.insert(book, false);

		assertEquals(1, springTestDao.getCount());
	}

	/**
	 * Tests SQL execution (rollback).
	 */
	public void testSpringSpringConnectionProvider3() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT);

		SpringTestDao springTestDao = (SpringTestDao) applicationContext.getBean("springTestDao");

		Book book = new Book();
		book.name = "Mirage in Action";
		book.author = "Naoki Takezoe";
		book.price = 20;
		try {
			springTestDao.insert(book, true);
			fail();
		} catch(RuntimeException ignored){
		}

		assertEquals(0, springTestDao.getCount());
	}

}
