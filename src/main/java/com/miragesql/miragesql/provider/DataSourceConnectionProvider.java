package com.miragesql.miragesql.provider;

import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import com.miragesql.miragesql.exception.SQLRuntimeException;
import com.miragesql.miragesql.util.JdbcUtil;

/**
 * {@link ConnectionProvider} implementation which gets a connection from <code>javax.sql.DataSource</code>.
 *
 * @author Naoki Takezoe
 */
public class DataSourceConnectionProvider implements ConnectionProvider {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConnectionProvider.class);

    private DataSource dataSource;

    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

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
    /**{@inheritDoc}*/
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
