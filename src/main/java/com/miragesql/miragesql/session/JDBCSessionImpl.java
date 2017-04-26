package com.miragesql.miragesql.session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.miragesql.miragesql.SqlManager;
import com.miragesql.miragesql.SqlManagerImpl;
import com.miragesql.miragesql.dialect.Dialect;
import com.miragesql.miragesql.exception.SessionException;
import com.miragesql.miragesql.provider.DefaultConnectionProvider;
import com.miragesql.miragesql.util.StringUtil;

/**
 * The default implementation of {@link Session}.
 * <p>
 * This implementation set {@link Dialect} to {@link SqlManager} automatically by the JDBC connection URL.
 *
 * @author Naoki Takezoe
 */
public class JDBCSessionImpl implements Session {

	private static final Logger logger = Logger.getLogger(JDBCSessionImpl.class.getName());

	private SqlManager sqlManager;
	private DefaultConnectionProvider provider;
	private String driver;
	private String url;
	private String user;
	private String password;
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
	 *     <li>sql.cache - if true then SqlManager caches parsing result of 2waySQL</li>
	 *   </ul>
	 */
	public JDBCSessionImpl(Properties properties){
		this.driver   = properties.getProperty("jdbc.driver");
		this.url      = properties.getProperty("jdbc.url");
		this.user     = properties.getProperty("jdbc.user");
		this.password = properties.getProperty("jdbc.password");

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
	}

	/**
	 * {@inheritDoc}
	 */
	public void begin() {
		if(logger.isLoggable(Level.INFO)){
			logger.info("Begin transaction.");
		}
		try {
			if(StringUtil.isNotEmpty(driver)){
				Class.forName(driver);
			}
			Connection conn = DriverManager.getConnection(url, user, password);
			conn.setAutoCommit(false);
			provider.setConnection(conn);
		} catch (ClassNotFoundException ex) {
			throw new SessionException("Driver class not found.", ex);
			
		} catch (SQLException ex){
			throw new SessionException("Failed to begin transaction.", ex);
			
		}
	}

	/**
	 * {@inheritDoc}
	 */
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

	public SqlManager getSqlManager() {
		return sqlManager;
	}

	public void release() {
		this.rollbackOnly.remove();

		if(provider instanceof DefaultConnectionProvider){
			((DefaultConnectionProvider) provider).releaseConnection();
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

	public void setRollbackOnly() {
		this.rollbackOnly.set(true);
	}

	public boolean isRollbackOnly() {
		return this.rollbackOnly.get() != null;
	}

}
