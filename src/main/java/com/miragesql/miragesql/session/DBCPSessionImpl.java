package com.miragesql.miragesql.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import com.miragesql.miragesql.SqlManager;
import com.miragesql.miragesql.SqlManagerImpl;
import com.miragesql.miragesql.exception.ConfigurationException;
import com.miragesql.miragesql.exception.SessionException;
import com.miragesql.miragesql.provider.DefaultConnectionProvider;
import com.miragesql.miragesql.util.StringUtil;

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
 * session.class=com.miragesql.miragesql.session.DBCPSessionImpl
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

	private static final Logger logger = LoggerFactory.getLogger(DBCPSessionImpl.class);

	private SqlManager sqlManager;
	private DefaultConnectionProvider provider;
	private DataSource dataSource;
	private ThreadLocal<Boolean> rollbackOnly = new ThreadLocal<>();

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

	/**{@inheritDoc}*/
	public void begin() {
		if(logger.isInfoEnabled()){
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

    /**{@inheritDoc}*/
	public void commit() {
		if(logger.isInfoEnabled()){
			logger.info("Commit transaction.");
		}
		try {
			provider.getConnection().commit();
		} catch (SQLException ex){
			throw new SessionException("Failed to commit transaction.", ex);
		}
	}

    /**{@inheritDoc}*/
	public void rollback() {
		if(logger.isInfoEnabled()){
			logger.info("Rollback transaction.");
		}
		try {
			provider.getConnection().rollback();
		} catch (SQLException ex){
			throw new SessionException("Failed to rollback transaction.", ex);
		}
	}

    /**{@inheritDoc}*/
	public void release() {
		this.rollbackOnly.remove();

		if(provider instanceof DefaultConnectionProvider){
			((DefaultConnectionProvider) provider).releaseConnection();
		}
	}

    /**{@inheritDoc}*/
	public SqlManager getSqlManager() {
		return sqlManager;
	}

    /**{@inheritDoc}*/
	public void setRollbackOnly() {
		this.rollbackOnly.set(true);
	}

    /**{@inheritDoc}*/
	public boolean isRollbackOnly() {
		return this.rollbackOnly.get() != null;
	}
}
