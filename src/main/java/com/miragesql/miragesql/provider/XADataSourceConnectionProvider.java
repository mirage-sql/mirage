package com.miragesql.miragesql.provider;

import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.XADataSource;

import com.miragesql.miragesql.exception.SQLRuntimeException;
import com.miragesql.miragesql.util.JdbcUtil;

/**
 * {@link ConnectionProvider} implementation which gets a connection from <code>javax.sql.XADataSource</code>.
 *
 * @author Naoki Takezoe
 */
public class XADataSourceConnectionProvider implements ConnectionProvider {

    private static final Logger logger = LoggerFactory.getLogger(XADataSourceConnectionProvider.class);

    private XADataSource xaDataSource;

    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    public void setDataSource(XADataSource xaDataSource){
        this.xaDataSource = xaDataSource;
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
                conn = xaDataSource.getXAConnection().getConnection();
                threadLocal.set(conn);
                logger.info("Get Connection from DataSource.");
            }

            return conn;

        } catch(SQLException ex){
            throw new SQLRuntimeException(ex);
        }
    }

}
