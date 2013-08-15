package jp.sf.amateras.mirage.dialect;

public class H2Dialect extends StandardDialect {
	
	@Override
	public String getName() {
		return "h2";
	}
	
	@Override
	public String getSequenceSql(String sequenceName) {
		return String.format("SELECT NEXTVAL('%s')", sequenceName);
	}

}
