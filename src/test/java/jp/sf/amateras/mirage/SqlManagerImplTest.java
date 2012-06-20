package jp.sf.amateras.mirage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.sf.amateras.mirage.annotation.Column;
import jp.sf.amateras.mirage.annotation.PrimaryKey;
import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import jp.sf.amateras.mirage.exception.BreakIterationException;
import jp.sf.amateras.mirage.test.ExecutedSQLInfo;
import jp.sf.amateras.mirage.test.MirageTestContext;
import jp.sf.amateras.mirage.test.MockSqlManager;

public class SqlManagerImplTest extends AbstractDatabaseTest {

	public void testGetSingleResultByMap() throws Exception {
		Book book = new Book();
		book.name = "Mirage in Action";
		book.author = "Naoki Takezoe";
		book.price = 20;

		sqlManager.insertEntity(book);

		Map<String, String> param = new HashMap<String, String>();
		param.put("bookName", "Mirage in Action");

		@SuppressWarnings("unchecked")
		Map<String, Object> map = sqlManager.getSingleResult(
				Map.class,
				SQL_PREFIX + "SqlManagerImplTest_selectByBookName.sql",
				param);

		assertEquals("Mirage in Action", map.get("bookName"));
		assertEquals("Naoki Takezoe", map.get("author"));
		assertEquals("20", map.get("price"));
	}

	public void testSelectIn() throws Exception {
		{
			Book book = new Book();
			book.name = "Mirage in Action";
			book.author = "Naoki Takezoe";
			book.price = 20;

			sqlManager.insertEntity(book);
		}
		{
			Book book = new Book();
			book.name = "Essential Mirage";
			book.author = "Naoki Takezoe";
			book.price = 30;

			sqlManager.insertEntity(book);
		}

		BookNamesParam param = new BookNamesParam();
		param.bookNames.add("Mirage in Action");
		param.bookNames.add("Essential Mirage");

		List<Book> results = sqlManager.getResultList(
				Book.class,
				SQL_PREFIX + "SqlManagerImplTest_selectByBookNames.sql",
				param);

		assertEquals(2, results.size());
		assertEquals("Mirage in Action", results.get(0).name);
		assertEquals("Essential Mirage", results.get(1).name);
	}

	public void testGetSingleResultByInteger() throws Exception {
		Book book = new Book();
		book.name = "Mirage in Action";
		book.author = "Naoki Takezoe";
		book.price = 20;

		sqlManager.insertEntity(book);
		sqlManager.insertEntity(book);

		Integer count = sqlManager.getSingleResult(
				Integer.class,
				SQL_PREFIX + "SqlManagerImplTest_countBook.sql");

		assertEquals(2, count.intValue());
	}

	public void testGetCount() throws Exception {
		Book book1 = new Book();
		book1.name = "Mirage in Action";
		book1.author = "Naoki Takezoe";
		book1.price = 20;

		Book book2 = new Book();
		book2.name = "Mirage in Action";
		book2.author = "Naoki Takezoe";
		book2.price = 20;

		sqlManager.insertEntity(book1);
		sqlManager.insertEntity(book2);
		assertEquals(new Long(0), book1.bookId);
		assertEquals(new Long(1), book2.bookId);

		Map<String, String> param = new HashMap<String, String>();
		param.put("bookName", "Mirage in Action");

		int count = sqlManager.getCount(
				SQL_PREFIX + "SqlManagerImplTest_selectByBookName.sql",
				param);

		assertEquals(2, count);
	}

	/**
	 * Removing semicolon.
	 */
	public void testRemoveSemicolon() throws Exception {
		MirageTestContext.initMirageTestContext();
		MockSqlManager sqlManager = new MockSqlManager();

		sqlManager.getResultList(
				Book.class, SQL_PREFIX + "SqlManagerImplTest_removeSemicolon.sql");

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

		sqlManager.getCount(SQL_PREFIX + "SqlManagerImplTest_testOracleHint1.sql");

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

		sqlManager.getCount(SQL_PREFIX + "SqlManagerImplTest_testOracleHint2.sql");

		MirageTestContext.verifySqlNumber(1);
		ExecutedSQLInfo info = MirageTestContext.getExecutedSQLInfo(0);
		assertEquals("SELECT COUNT(*) FROM (select --+ first_rows(1)\r\nBOOK_ID, BOOK_NAME from BOOK)",
				info.getSql());
	}

	public void testInsertEntity() throws Exception {
		Book book = new Book();
		book.name = "Mirage in Action";
		book.author = "Naoki Takezoe";
		book.price = 20;

		sqlManager.insertEntity(book);
		assertEquals(new Long(0), book.bookId);

		BookParam param = new BookParam();
		param.bookName = "Mirage in Action";

		List<Book> bookList = sqlManager.getResultList(
				Book.class,
				SQL_PREFIX + "SqlManagerImplTest_selectByBookName.sql",
				param);

		assertEquals(1, bookList.size());
		assertEquals("Mirage in Action", bookList.get(0).name);
		assertEquals("Naoki Takezoe", bookList.get(0).author);
		assertEquals(new Integer(20), bookList.get(0).price);
	}

	public void testInsertEntity_Sequence() throws Exception {
		UserInfo userInfo1 = new UserInfo();
		userInfo1.userName = "Naoki Takezoe";

		UserInfo userInfo2 = new UserInfo();
		userInfo2.userName = "Coharu Takezoe";

		sqlManager.insertEntity(userInfo1);
		sqlManager.insertEntity(userInfo2);
		assertEquals(new Long(0), userInfo1.userId);
		assertEquals(new Long(1), userInfo2.userId);

		List<UserInfo> userList = sqlManager.getResultListBySql(
				UserInfo.class,
				"SELECT * FROM USER_INFO ORDER BY USER_ID");

		assertEquals(2, userList.size());
		assertEquals(new Long(0), userList.get(0).userId);
		assertEquals("Naoki Takezoe", userList.get(0).userName);
		assertEquals(new Long(1), userList.get(1).userId);
		assertEquals("Coharu Takezoe", userList.get(1).userName);
	}

	public void testInsertEntityWithNullProperty() throws Exception {
		Book book = new Book();
		book.name = "Mirage in Action";
		book.author = "Naoki Takezoe";
		book.price = null;

		sqlManager.insertEntity(book);
		assertEquals(new Long(0), book.bookId);

		BookParam param = new BookParam();
		param.bookName = "Mirage in Action";

		List<Book> bookList = sqlManager.getResultList(
				Book.class,
				SQL_PREFIX + "SqlManagerImplTest_selectByBookName.sql",
				param);

		assertEquals(1, bookList.size());
		assertEquals("Mirage in Action", bookList.get(0).name);
		assertEquals("Naoki Takezoe", bookList.get(0).author);
		assertNull(bookList.get(0).price);
	}

	public void testInsertBatchArray() throws Exception {
		Book book1 = new Book();
		book1.name = "Mirage in Action";
		book1.author = "Naoki Takezoe";
		book1.price = 20;

		Book book2 = new Book();
		book2.name = "Mastering Seasar2";
		book2.author = "Naoki Takezoe";
		book2.price = 25;

		int result = sqlManager.insertBatch(book1, book2);
		assertEquals(2, result);
		assertEquals(new Long(0), book1.bookId);
		assertEquals(new Long(1), book2.bookId);

		List<Book> bookList = sqlManager.getResultList(
				Book.class,
				SQL_PREFIX + "SqlManagerImplTest_selectByBookName.sql",
				new BookParam());

		assertEquals(2, bookList.size());

		assertEquals("Mirage in Action", bookList.get(0).name);
		assertEquals("Naoki Takezoe", bookList.get(0).author);
		assertEquals(new Integer(20), bookList.get(0).price);

		assertEquals("Mastering Seasar2", bookList.get(1).name);
		assertEquals("Naoki Takezoe", bookList.get(1).author);
		assertEquals(new Integer(25), bookList.get(1).price);
	}

	public void testInsertBatchList() throws Exception {
		Book book1 = new Book();
		book1.name = "Mirage in Action";
		book1.author = "Naoki Takezoe";
		book1.price = 20;

		Book book2 = new Book();
		book2.name = "Mastering Seasar2";
		book2.author = "Naoki Takezoe";
		book2.price = 25;

		List<Book> books = new ArrayList<Book>();
		books.add(book1);
		books.add(book2);

		int result = sqlManager.insertBatch(books);
		assertEquals(2, result);

		List<Book> bookList = sqlManager.getResultList(
				Book.class,
				SQL_PREFIX + "SqlManagerImplTest_selectByBookName.sql",
				new BookParam());

		assertEquals(2, bookList.size());

		assertEquals("Mirage in Action", bookList.get(0).name);
		assertEquals("Naoki Takezoe", bookList.get(0).author);
		assertEquals(new Integer(20), bookList.get(0).price);

		assertEquals("Mastering Seasar2", bookList.get(1).name);
		assertEquals("Naoki Takezoe", bookList.get(1).author);
		assertEquals(new Integer(25), bookList.get(1).price);
	}


	public void testUpdateEntity() throws Exception {
		Book book = new Book();
		book.name = "Mirage in Action";
		book.author = "Naoki Takezoe";
		book.price = 20;

		sqlManager.insertEntity(book);

		BookParam param = new BookParam();
		param.bookName = "Mirage in Action";

		book = sqlManager.getSingleResult(
				Book.class,
				SQL_PREFIX + "SqlManagerImplTest_selectByBookName.sql",
				param);

		book.price = 100;
		sqlManager.updateEntity(book);

		List<Book> bookList = sqlManager.getResultList(
				Book.class,
				SQL_PREFIX + "SqlManagerImplTest_selectByBookName.sql");

		assertEquals(1, bookList.size());
		assertEquals("Mirage in Action", bookList.get(0).name);
		assertEquals("Naoki Takezoe", bookList.get(0).author);
		assertEquals(new Integer(100), bookList.get(0).price);
	}

	public void testDeleteEntity() throws Exception {
		Book book = new Book();
		book.name = "Mirage in Action";
		book.author = "Naoki Takezoe";
		book.price = 20;

		sqlManager.insertEntity(book);

		BookParam param = new BookParam();
		param.bookName = "Mirage in Action";

		book = sqlManager.getSingleResult(
				Book.class,
				SQL_PREFIX + "SqlManagerImplTest_selectByBookName.sql",
				param);

		sqlManager.deleteEntity(book);

		book = sqlManager.getSingleResult(
				Book.class,
				SQL_PREFIX + "SqlManagerImplTest_selectByBookName.sql",
				param);

		assertNull(book);
	}

	public void testFindEntity(){
		Book book = new Book();
		book.name = "Mirage in Action";
		book.author = "Naoki Takezoe";
		book.price = 20;

		sqlManager.insertEntity(book);

		book = sqlManager.findEntity(Book.class, 0);
		assertEquals("Mirage in Action", book.name);
		assertEquals("Naoki Takezoe", book.author);
		assertEquals(new Integer(20), book.price);

		book = sqlManager.findEntity(Book.class, 1);
		assertNull(book);
	}

	public void testIterate() throws Exception {
		sqlManager.insertEntity(new Book("Mirage in Action", "Naoki Takezoe", 20));
		sqlManager.insertEntity(new Book("Click in Action", "Bob Schellink", 30));
		sqlManager.insertEntity(new Book("Introduction of Seasar2", "Naoki Takezoe", 30));

		Integer result = sqlManager.iterate(
				Book.class,
				new IterationCallback<Book, Integer>() {

					private int result;

					public Integer iterate(Book entity) {
						result = result + entity.price;
						return result;
					}
				},
				SQL_PREFIX + "SqlManagerImplTest_selectByBookName.sql");

		assertEquals(80, result.intValue());
	}

	public void testIterateBreak() throws Exception {
		sqlManager.insertEntity(new Book("Mirage in Action", "Naoki Takezoe", 20));
		sqlManager.insertEntity(new Book("Click in Action", "Bob Schellink", 30));
		sqlManager.insertEntity(new Book("Introduction of Seasar2", "Naoki Takezoe", 30));

		Integer result = sqlManager.iterate(
				Book.class,
				new IterationCallback<Book, Integer>() {

					private int result;

					public Integer iterate(Book entity) {
						if(result >= 50){
							throw new BreakIterationException();
						}
						result = result + entity.price;
						return result;
					}
				},
				SQL_PREFIX + "SqlManagerImplTest_selectByBookName.sql");

		assertEquals(50, result.intValue());
	}

	public void testCall() throws Exception {
		try {
			sqlManager.executeUpdate(SQL_PREFIX + "SqlManagerImplTest_testCall_setUp.sql");

			NewBookParam newBookParam = new NewBookParam();
			newBookParam.bookName = "Seasar2徹底入門";
			newBookParam.author = "Naoki Takezoe";
			newBookParam.price = 3990;
			sqlManager.call("NEW_BOOK", newBookParam);

			Book book = sqlManager.findEntity(Book.class, 0);
			assertEquals("Seasar2徹底入門", book.name);
			assertEquals("Naoki Takezoe", book.author);
			assertEquals(3990, book.price.intValue());

		} finally {
			sqlManager.executeUpdate(SQL_PREFIX + "SqlManagerImplTest_testCall_tearDown.sql");
		}
	}

	public static class BookParam {
		public String bookName;
	}

	public static class BookNamesParam {
		public List<String> bookNames = new ArrayList<String>();
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

}
