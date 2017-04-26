package com.miragesql.miragesql.bean;

import com.miragesql.miragesql.annotation.PrimaryKey;
import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;

public class Magazine {
	public static final String CONSTANT = "foobar";
	
	@PrimaryKey(generationType=GenerationType.IDENTITY)
	private long magazineId;
	
	public String magazineCode;
	
	int price;
	
	public long getId() {
		return magazineId;
	}

	public void setId(long id) {
		this.magazineId = id;
	}
}