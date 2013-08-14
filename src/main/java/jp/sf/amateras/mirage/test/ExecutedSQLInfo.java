package jp.sf.amateras.mirage.test;

/**
 *
 * @author Naoki Takezoe
 */
public class ExecutedSQLInfo {

	private String sql;
	private Object[] params;

	/**
	 * Constructor.
	 *
	 * @param sql the executed SQL
	 * @param params the array of bound parameters
	 */
	public ExecutedSQLInfo(String sql, Object[] params){
		this.sql = sql;
		this.params = params;
	}

	/**
	 * Returns the executed SQL.
	 *
	 * @return the executed SQL
	 */
	public String getSql(){
		return this.sql;
	}

	/**
	 * Returns the array of bound parameters.
	 *
	 * @return the array of bound parameters
	 */
	public Object[] getParams(){
		if(params == null){
			params = new Object[0];
		}
		return this.params;
	}

}
