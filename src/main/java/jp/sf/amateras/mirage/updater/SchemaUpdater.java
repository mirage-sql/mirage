package jp.sf.amateras.mirage.updater;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import jp.sf.amateras.mirage.SqlManager;
import jp.sf.amateras.mirage.SqlManagerImpl;
import jp.sf.amateras.mirage.dialect.Dialect;
import jp.sf.amateras.mirage.exception.SQLRuntimeException;
import jp.sf.amateras.mirage.util.IOUtil;

/**
 * Execute SQL files which located a specific package to update database schema.
 * You can use this to update database schema automatically  with application updating.
 * <p>
 * By the default, you have to locate SQLs within META-INF as follows:
 * </p>
 * <pre>
 * /META-INF
 *   mysql_1.sql
 *   mysql_2.sql
 *   mysql_3.sql
 *   ...
 * </pre>
 * <p>
 * You can change a package where locate SQL files by {@link #setPackageName(String)}.
 * </p>
 * <p>
 * SQLs will be executed in the order of version number which contained in the filename.
 * The version number (a part of filename) have to be a continued integer value.
 * Filename can contain the database type such as "mysql" also. It is omittable.
 * For examples, if the schema updater can not find a "mysql_1.sql", uses "1.sql" instead of it.
 * </p>
 *
 * @author Naoki Takezoe
 */
public class SchemaUpdater {

	private static final Logger logger = Logger.getLogger(SchemaUpdater.class.getName());

	protected SqlManager sqlManager;

	protected String tableName = "SCHEMA_VERSION";

	protected String packageName = "META-INF";

	/**
	 * Sets the SqlManager to use for schema updating.
	 *
	 * @param sqlManager the SqlManager
	 */
	public void setSqlManager(SqlManager sqlManager){
		this.sqlManager = sqlManager;
	}

	/**
	 * Sets the table name which manages the schema version. The default is "SCHEMA_VERSION".
	 *
	 * @param tableName the table name
	 */
	public void setTableName(String tableName){
		this.tableName = tableName;
	}

	/**
	 * Sets the package name where locate SQL files. The default is "META-INF".
	 *
	 * @param packageName the package name
	 */
	public void setPackageName(String packageName){
		this.packageName = packageName;
	}

	/**
	 * Updates database schema to the latest state.
	 * <p>
	 * Note: Before calling this method, Connection might have to be auto-commit mode.
	 */
	public void update(){
		logger.info("Begin automatic schema updating.");
		if(!existsTable()){
			createTable();
		}

		int currentVersion = getCurrentVersion();
		int version = currentVersion;
		String sql = null;

		while((sql = getSql(version + 1)) != null){
			if(sql.trim().length() != 0){
				sqlManager.executeUpdateBySql(sql);
			}
			version++;
		}

		if(version != currentVersion){
			updateVersion(version);
			logger.info(String.format("Schema was updated to version %d.", version));
		} else {
			logger.info("There are no updates.");
		}

		logger.info("End automatic schema updating.");
	}

	/**
	 * Returns the SQL which located within a package specified by the packageName as
	 * "dialectname_version.sql" or "version.sql".
	 *
	 * @param version the version number
	 * @return SQL or null if the SQL file does not exist
	 */
	protected String getSql(int version){
		Dialect dialect = ((SqlManagerImpl) sqlManager).getDialect();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream in = classLoader.getResourceAsStream(
				String.format("%s/%s_%d.sql", packageName, dialect.getName().toLowerCase(), version));

		if(in == null){
			in = classLoader.getResourceAsStream(
					String.format("%s/%d.sql", packageName, version));

			if(in == null){
				return null;
			}
		}

		byte[] buf = IOUtil.readStream(in);

		try {
			return new String(buf, "UTF-8");

		} catch(UnsupportedEncodingException ex){
			// must not to be reached here
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Checks the table which manages a schema version exists or not exisis.
	 *
	 * @return if the table exists then returns true; otherwise false
	 */
	protected boolean existsTable(){
		try {
			int count = sqlManager.getSingleResultBySql(Integer.class,
					String.format("SELECT COUNT(*) FROM %s", tableName));

			if(count == 0){
				// TODO Should insert an initial record?
				return false;
			}
			return true;

		} catch(SQLRuntimeException ex){
			return false;
		}
	}

	/**
	 * Creates table which manages schema version and insert an initial record as version 0.
	 */
	protected void createTable(){
		sqlManager.executeUpdateBySql(
				String.format("CREATE TABLE %s (VERSION NUMERIC NOT NULL)", tableName));

		sqlManager.executeUpdateBySql(
				String.format("INSERT INTO %s (0))", tableName));
	}

	/**
	 * Returns the current version number.
	 *
	 * @return the current version number
	 */
	protected int getCurrentVersion(){
		return sqlManager.getSingleResultBySql(Integer.class,
				String.format("SELECT COUNT(*) FROM %s", tableName));
	}

	/**
	 * Updates the version number by the given value.
	 *
	 * @param version the version number
	 */
	protected void updateVersion(int version){
		sqlManager.executeUpdateBySql(
				String.format("UPDATE %s SET VERSION=?", tableName), version);
	}

}
