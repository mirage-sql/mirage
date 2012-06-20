package jp.sf.amateras.mirage.bean;

import jp.sf.amateras.mirage.annotation.PrimaryKey;
import jp.sf.amateras.mirage.annotation.Transient;
import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;

public class Book {
	@PrimaryKey(generationType=GenerationType.IDENTITY)
	private Integer bookId;
	private String bookName;

	public Integer getBookId() {
		return bookId;
	}
	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}
	@Transient
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
}