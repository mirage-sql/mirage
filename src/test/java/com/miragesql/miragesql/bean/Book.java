package com.miragesql.miragesql.bean;

import com.miragesql.miragesql.annotation.Enumerated;
import com.miragesql.miragesql.annotation.Enumerated.EnumType;
import com.miragesql.miragesql.annotation.PrimaryKey;
import com.miragesql.miragesql.annotation.Transient;
import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;

public class Book {
	@PrimaryKey(generationType=GenerationType.IDENTITY)
	private Integer bookId;
	private String bookName;
	
	@Enumerated(EnumType.STRING)
	private BookType bookType;

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
	public BookType getBookType() {
		return bookType;
	}
	public void setBookType(BookType bookType) {
		this.bookType = bookType;
	}
}