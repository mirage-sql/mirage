package jp.sf.amateras.mirage.bean;

import jp.sf.amateras.mirage.annotation.PrimaryKey;
import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;

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