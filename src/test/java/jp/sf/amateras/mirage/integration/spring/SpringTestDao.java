package jp.sf.amateras.mirage.integration.spring;

import jp.sf.amateras.mirage.SqlManager;
import jp.sf.amateras.mirage.SqlManagerImplTest.Book;

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
		return sqlManager.getSingleResultBySql(
				Integer.class, "SELECT COUNT(*) FROM BOOK");
	}

}
