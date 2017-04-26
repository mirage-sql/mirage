package jp.sf.amateras.mirage.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import jp.sf.amateras.mirage.SqlManager;
import jp.sf.amateras.mirage.SqlManagerImpl;
import jp.sf.amateras.mirage.exception.ConfigurationException;
import jp.sf.amateras.mirage.exception.SessionException;
import jp.sf.amateras.mirage.provider.DefaultConnectionProvider;
import jp.sf.amateras.mirage.util.StringUtil;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPoolFactory;

/**
 * The implementation of {@link Session} which gets the connection from the DBCP connection pool.
 * <p>
 * To enable DBCP, you have to make jdbc.properties as follows:
 * <pre>
 * session.class=jp.sf.amateras.mirage.session.DBCPSessionImpl
 * jdbc.driver=com.mysql.jdbc.Driver
 * jdbc.url=jdbc:mysql://localhost/test
 * jdbc.user=root
 * jdbc.password=
 * dbcp.max_active=100
 * dbcp.min_idle=10
 * dbcp.max_wait=5000
 * </pre>
 *
 * @author Naoki Takezoe
 */
public class DBCPSessionImpl implements Session {

	private static final Logger logger = Logger.getLogger(DBCPSessionImpl.class.getName());

	private SqlManager sqlManager;
	private DefaultConnectionProvider provider;
	private DataSource dataSource;
	private ThreadLocal<Boolean> rollbackOnly = new ThreadLocal<Boolean>();

	/**
	 * The constructor.
	 *
	 * @param properties the Properties object which has a following properties:
	 *   <ul>
	 *     <li>jdbc.driver - the JDBC driver classname (optional on JDBC 4.0)</li>
	 *     <li>jdbc.url - the JDBC connection URL</li>
	 *     <li>jdbc.user - the username</li>
	 *     <li>jdbc.password - the password</li>
	 *     <li>dbcp.max_active - max active connections (optional, default value is 100)</li>
	 *     <li>dbcp.min_idle - minimum idle time (optional, default value is 10)</li>
	 *     <li>dbcp.max_wait - max wait time (optional, default value is 5000)</li>
	 *     <li>sql.cache - if true then SqlManager caches parsing result of 2waySQL</li>
	 *   </ul>
	 */
	public DBCPSessionImpl(Properties properties){
		String driver   = properties.getProperty("jdbc.driver");
		String url      = properties.getProperty("jdbc.url");
		String user     = properties.getProperty("jdbc.user");
		String password = properties.getProperty("jdbc.password");

		int maxActive = 100;
		if(StringUtil.isNotEmpty(properties.getProperty("dbcp.max_active"))){
			maxActive = Integer.parseInt(properties.getProperty("dbcp.max_active"));
		}

		int minIdle = 10;
		if(StringUtil.isNotEmpty(properties.getProperty("dbcp.min_idle"))){
			minIdle = Integer.parseInt(properties.getProperty("dbcp.min_idle"));
		}

		int maxWait = 5000;
		if(StringUtil.isNotEmpty(properties.getProperty("dbcp.max_wait"))){
			maxWait = Integer.parseInt(properties.getProperty("dbcp.max_wait"));
		}

		sqlManager = new SqlManagerImpl();
		sqlManager.setDialect(DialectAutoSelector.getDialect(url));
		provider = new DefaultConnectionProvider();
		sqlManager.setConnectionProvider(provider);

		String cache = properties.getProperty("sql.cache");
		if("true".equals(cache)){
			((SqlManagerImpl) sqlManager).setCacheMode(true);
		} else {
			((SqlManagerImpl) sqlManager).setCacheMode(false);
		}

		try {
			if(StringUtil.isNotEmpty(driver)){
				Class.forName(driver);
			}
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException(e);
			
		}

		GenericObjectPool pool = new GenericObjectPool();
		pool.setMaxActive(maxActive);
		pool.setMinIdle(minIdle);
		pool.setMaxWait(maxWait);
		pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);

		ConnectionFactory factory = new DriverManagerConnectionFactory(url, user, password);

		KeyedObjectPoolFactory pstFactory = new StackKeyedObjectPoolFactory(50, 10);

		PoolableConnectionFactory poolFactory = new PoolableConnectionFactory(
				factory, pool, pstFactory, null, false, true);

		dataSource = new PoolingDataSource(poolFactory.getPool());
	}

	public void begin() {
		if(logger.isLoggable(Level.INFO)){
			logger.info("Begin transaction.");
		}
		try {
			Connection conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			provider.setConnection(conn);
		} catch (SQLException ex){
			throw new SessionException("Failed to begin transaction.", ex);
		}
	}

	public void commit() {
		if(logger.isLoggable(Level.INFO)){
			logger.info("Commit transaction.");
		}
		try {
			provider.getConnection().commit();
		} catch (SQLException ex){
			throw new SessionException("Failed to commit transaction.", ex);
		}
	}

	public void rollback() {
		if(logger.isLoggable(Level.INFO)){
			logger.info("Rollback transaction.");
		}
		try {
			provider.getConnection().rollback();
		} catch (SQLException ex){
			throw new SessionException("Failed to rollback transaction.", ex);
		}
	}

	public void release() {
		this.rollbackOnly.remove();

		if(provider instanceof DefaultConnectionProvider){
			((DefaultConnectionProvider) provider).releaseConnection();
		}
	}

	public SqlManager getSqlManager() {
		return sqlManager;
	}

	public void setRollbackOnly() {
		this.rollbackOnly.set(true);
	}

	public boolean isRollbackOnly() {
		return this.rollbackOnly.get() != null;
	}
}
