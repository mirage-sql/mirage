package jp.sf.amateras.mirage.session;

import jp.sf.amateras.mirage.dialect.*;

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
		} else if(url.startsWith("jdbc:derby:")){
			return new DerbyDialect();
		}
		return new StandardDialect();
	}

}
