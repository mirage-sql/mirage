package jp.sf.amateras.mirage.session;

import jp.sf.amateras.mirage.dialect.Dialect;
import jp.sf.amateras.mirage.dialect.H2Dialect;
import jp.sf.amateras.mirage.dialect.HyperSQLDialect;
import jp.sf.amateras.mirage.dialect.MySQLDialect;
import jp.sf.amateras.mirage.dialect.OracleDialect;
import jp.sf.amateras.mirage.dialect.PostgreSQLDialect;
import jp.sf.amateras.mirage.dialect.StandardDialect;

public class DialectAutoSelector {

	public static Dialect getDialect(String url){
		if(url.startsWith("jdbc:mysql:")){
			return new MySQLDialect();
		} else if(url.startsWith("jdbc:postgresql:")){
			return new PostgreSQLDialect();
		} else if(url.startsWith("jdbc:oracle:")){
			return new OracleDialect();
		} else if(url.startsWith("jdbc:hsqldb:")){
			return new HyperSQLDialect();
		} else if(url.startsWith("jdbc:h2:")){
			return new H2Dialect();
		}
		return new StandardDialect();
	}

}
