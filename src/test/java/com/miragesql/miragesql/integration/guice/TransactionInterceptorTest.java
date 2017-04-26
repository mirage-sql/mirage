package com.miragesql.miragesql.integration.guice;

import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import com.miragesql.miragesql.SqlManager;
import com.miragesql.miragesql.StringSqlResource;
import com.miragesql.miragesql.session.Session;
import com.miragesql.miragesql.session.SessionFactory;
import com.miragesql.miragesql.test.MirageTestContext;
import com.miragesql.miragesql.test.MockSqlManager;
import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;

public class TransactionInterceptorTest extends TestCase {

	@Mock
	private Session session;

	@Override
	public void setUp() throws Exception {
		MirageTestContext.initMirageTestContext();
		MockitoAnnotations.initMocks(this);

		// Sets the mock session to SessionFactory
		Field field = SessionFactory.class.getDeclaredField("session");
		field.setAccessible(true);
		field.set(null, session);
	}

	@Override
	public void tearDown() throws Exception {
		// Clears SessionFactory
		Field field = SessionFactory.class.getDeclaredField("session");
		field.setAccessible(true);
		field.set(null, null);
	}

	public void testCommit() throws Exception {
		MirageTestContext.addResult(100);

		Injector injector = Guice.createInjector(new TestModule());
		TestDao1 dao = injector.getInstance(TestDao1.class);
		int result = dao.getCount();

		assertEquals(100, result);

		verify(session, times(1)).begin();
		verify(session, times(1)).commit();
		verify(session, never()).rollback();
		//verify(session, times(1)).release();
	}

	public void testRollback() throws Exception {
		Injector injector = Guice.createInjector(new TestModule());
		TestDao2 dao = injector.getInstance(TestDao2.class);

		try {
			dao.getCount();
			fail();
		} catch(RuntimeException ex){
		}

		verify(session, times(1)).begin();
		verify(session, times(1)).rollback();
		verify(session, never()).commit();
	}

	public static class TestModule extends AbstractModule {

		@Override
		protected void configure() {
			bindInterceptor(
					Matchers.any(),
					Matchers.annotatedWith(Transactional.class),
					new TransactionInterceptor());
		}

		@Provides
		@Singleton
		public SqlManager getSqlManager(){
			return new MockSqlManager();
		}

	}

	public static class TestDao1 {

		@Inject
		private SqlManager sqlManager;

		@Transactional
		public int getCount(){
			return sqlManager.getSingleResult(Integer.class,
					new StringSqlResource("SELECT COUNT(*) AS COUNT FROM EMPLOYEE"));
		}

	}

	public static class TestDao2 {

		@Transactional
		public int getCount(){
			throw new RuntimeException();
		}

	}
}
