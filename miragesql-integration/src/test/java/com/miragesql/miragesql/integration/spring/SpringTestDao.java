package com.miragesql.miragesql.integration.spring;

import com.miragesql.miragesql.SqlManager;
import com.miragesql.miragesql.StringSqlResource;
import com.miragesql.miragesql.SqlManagerImplTest.Book;

import org.springframework.transaction.annotation.Transactional;

public class SpringTestDao {

	private SqlManager sqlManager;

	public SqlManager getSqlManager() {
		return sqlManager;
	}

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	@Transactional
	public void insert(Book book, boolean throwException){
		sqlManager.insertEntity(book);
		if(throwException){
			throw new RuntimeException();
		}
	}

	@Transactional
	public int getCount(){
		return sqlManager.getSingleResult(
				Integer.class, new StringSqlResource("SELECT COUNT(*) FROM BOOK"));
	}

}
