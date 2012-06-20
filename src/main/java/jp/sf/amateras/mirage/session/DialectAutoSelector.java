package jp.sf.amateras.mirage.session;

import jp.sf.amateras.mirage.dialect.Dialect;
import jp.sf.amateras.mirage.dialect.HyperSQLDialect;
import jp.sf.amateras.mirage.dialect.MySQLDialect;
import jp.sf.amateras.mirage.dialect.OracleDialect;
import jp.sf.amateras.mirage.dialect.PostgreDialect;
import jp.sf.amateras.mirage.dialect.StandardDialect;

public class DialectAutoSelector {

	public static Dialect getDialect(String url){
		if(url.startsWith("jdbc:mysql:")){
			return new MySQLDialect();
		} else if(url.startsWith("jdbc:postgresql:")){
			return new PostgreDialect();
		} else if(url.startsWith("jdbc:oracle:")){
			return new OracleDialect();
		} else if(url.startsWith("jdbc:hsqldb:")){
			return new HyperSQLDialect();
		}
		return new StandardDialect();
	}

}
