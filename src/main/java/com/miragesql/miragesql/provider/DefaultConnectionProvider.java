package com.miragesql.miragesql.provider;

import java.sql.Connection;
import java.util.logging.Logger;

import com.miragesql.miragesql.util.JdbcUtil;

/**
 * The simple implementation of {@link ConnectionProvider}.
 *
 * @author Naoki Takezoe
 */
public class DefaultConnectionProvider implements ConnectionProvider {

	private static final Logger logger = Logger.getLogger(DefaultConnectionProvider.class.getName());

	private ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

	public void setConnection(Connection conn){
		threadLocal.set(conn);
	}

	public void releaseConnection(){
		Connection conn = threadLocal.get();
		if(conn != null){
			JdbcUtil.close(conn);
			threadLocal.remove();

			logger.info("Connection is released.");

		} else {
			logger.info("Connection is not used.");
		}
	}

//	@Override
	public Connection getConnection() {
		return threadLocal.get();
	}

}
