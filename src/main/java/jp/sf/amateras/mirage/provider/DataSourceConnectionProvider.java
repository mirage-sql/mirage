package jp.sf.amateras.mirage.provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import jp.sf.amateras.mirage.exception.SQLRuntimeException;
import jp.sf.amateras.mirage.util.JdbcUtil;

/**
 * {@link ConnectionProvider} implementation which gets a connection from <code>javax.sql.DataSource</code>.
 *
 * @author Naoki Takezoe
 */
public class DataSourceConnectionProvider implements ConnectionProvider {

	private static final Logger logger = Logger.getLogger(DataSourceConnectionProvider.class.getName());

	private DataSource dataSource;

	private ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
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
		try {
			Connection conn = threadLocal.get();

			if(conn == null){
				conn = dataSource.getConnection();
				threadLocal.set(conn);
				logger.info("Get Connection from DataSource.");
			}

			return conn;

		} catch(SQLException ex){
			throw new SQLRuntimeException(ex);
		}
	}

}
