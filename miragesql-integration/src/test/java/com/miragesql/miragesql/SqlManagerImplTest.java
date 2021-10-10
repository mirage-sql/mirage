package com.miragesql.miragesql;

import java.util.ArrayList;
import java.util.List;

import com.miragesql.miragesql.annotation.Column;
import com.miragesql.miragesql.annotation.Enumerated;
import com.miragesql.miragesql.annotation.Enumerated.EnumType;
import com.miragesql.miragesql.annotation.PrimaryKey;
import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import com.miragesql.miragesql.test.ExecutedSQLInfo;
import com.miragesql.miragesql.test.MirageTestContext;
import com.miragesql.miragesql.test.MockSqlManager;

public class SqlManagerImplTest extends AbstractDatabaseTest {


	/**
	 * Removing semicolon.
	 */
	public void testRemoveSemicolon() throws Exception {
		MirageTestContext.initMirageTestContext();
		MockSqlManager sqlManager = new MockSqlManager();

		sqlManager.getResultList(
				Book.class,
				new ClasspathSqlResource(SQL_PREFIX + "SqlManagerImplTest_removeSemicolon.sql"));

		MirageTestContext.verifySqlNumber(1);
		ExecutedSQLInfo info = MirageTestContext.getExecutedSQLInfo(0);
		assertEquals("SELECT BOOK_ID, BOOK_NAME, AUTHOR, PRICE FROM BOOK", info.getSql());
	}

	/**
	 * for Oracle hint
	 * <p>
	 * TODO Should it be written in SqlParserImplTest?
	 */
	public void testIOracleHint1() throws Exception {
		MirageTestContext.initMirageTestContext();
		MockSqlManager sqlManager = new MockSqlManager();

		sqlManager.getCount(new ClasspathSqlResource(SQL_PREFIX + "SqlManagerImplTest_testOracleHint1.sql"));

		MirageTestContext.verifySqlNumber(1);
		ExecutedSQLInfo info = MirageTestContext.getExecutedSQLInfo(0);
		assertEquals("SELECT COUNT(*) FROM (select /*+ first_rows(1) */BOOK_ID, BOOK_NAME from BOOK)",
				info.getSql());
	}

	/**
	 * for Oracle hint
	 * <p>
	 * TODO Should it be written in SqlParserImplTest?
	 */
	public void testIOracleHint2() throws Exception {
		MirageTestContext.initMirageTestContext();
		MockSqlManager sqlManager = new MockSqlManager();

		sqlManager.getCount(new ClasspathSqlResource(SQL_PREFIX + "SqlManagerImplTest_testOracleHint2.sql"));

		MirageTestContext.verifySqlNumber(1);
		ExecutedSQLInfo info = MirageTestContext.getExecutedSQLInfo(0);
		assertEquals("SELECT COUNT(*) FROM (select --+ first_rows(1)\nBOOK_ID, BOOK_NAME from BOOK)",
				info.getSql());
	}

	public static class BookParam {
		public String bookName;
	}

	public static class BookNamesParam {
		public List<String> bookNames = new ArrayList<>();
	}

	public static class Book {
		@PrimaryKey(generationType=GenerationType.IDENTITY)
		public Long bookId;
		
		@Column(name="BOOK_NAME")
		public String name;
		
		public String author;
		
		public Integer price;

		public Book(){
		}

		public Book(String bookName, String author, Integer price){
			this.name = bookName;
			this.author = author;
			this.price = price;
		}
	}

	public static class NewBookParam {
		public String bookName;
		public String author;
		public Integer price;
	}

	public static class UserInfo {
		@PrimaryKey(generationType=GenerationType.SEQUENCE, generator="USER_INFO_USER_ID_SEQ")
		public Long userId;
		public String userName;
	}

	public static class Magazine {
		
		@PrimaryKey(generationType=GenerationType.IDENTITY)
		public Long magazineId;
		
		@Column(name="BOOK_NAME")
		public String name;
		
		@Enumerated(EnumType.STRING)
		public MagazineType magType;
		
		public Integer price;
		
		public enum MagazineType {
			TYPE_A,
			TYPE_B
		}
	}
}
