package com.miragesql.miragesql;

import com.miragesql.miragesql.annotation.Column;
import com.miragesql.miragesql.annotation.Enumerated;
import com.miragesql.miragesql.annotation.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

public class SqlManagerImplTest extends AbstractDatabaseTest {

    public void testInsertBatchList() throws Exception {
        Book book1 = new Book();
        book1.name = "Mirage in Action";
        book1.author = "Naoki Takezoe";
        book1.price = 20;

        Book book2 = new Book();
        book2.name = "Mastering Seasar2";
        book2.author = "Naoki Takezoe";
        book2.price = 25;

        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);

        int result = sqlManager.insertBatch(books);
        assertEquals(2, result);

        List<Book> bookList = sqlManager.getResultList(
                Book.class,
                new ClasspathSqlResource(SQL_PREFIX + "SqlManagerImplTest_selectByBookName.sql"),
                new BookParam());

        assertEquals(2, bookList.size());

        assertEquals("Mirage in Action", bookList.get(0).name);
        assertEquals("Naoki Takezoe", bookList.get(0).author);
        assertEquals(new Integer(20), bookList.get(0).price);

        assertEquals("Mastering Seasar2", bookList.get(1).name);
        assertEquals("Naoki Takezoe", bookList.get(1).author);
        assertEquals(new Integer(25), bookList.get(1).price);
    }

    public static class BookParam {
        public String bookName;
    }

    public static class BookNamesParam {
        public List<String> bookNames = new ArrayList<>();
    }

    public static class Book {
        @PrimaryKey(generationType= PrimaryKey.GenerationType.IDENTITY)
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
        @PrimaryKey(generationType= PrimaryKey.GenerationType.SEQUENCE, generator="USER_INFO_USER_ID_SEQ")
        public Long userId;
        public String userName;
    }

    public static class Magazine {

        @PrimaryKey(generationType= PrimaryKey.GenerationType.IDENTITY)
        public Long magazineId;

        @Column(name="BOOK_NAME")
        public String name;

        @Enumerated(Enumerated.EnumType.STRING)
        public MagazineType magType;

        public Integer price;

        public enum MagazineType {
            TYPE_A,
            TYPE_B
        }
    }

}
