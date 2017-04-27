package com.miragesql.miragesql.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.PropertyElf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import com.miragesql.miragesql.SqlManager;
import com.miragesql.miragesql.SqlManagerImpl;
import com.miragesql.miragesql.exception.ConfigurationException;
import com.miragesql.miragesql.exception.SessionException;
import com.miragesql.miragesql.provider.DefaultConnectionProvider;
import com.miragesql.miragesql.util.StringUtil;


/**
 * The implementation of {@link Session} which gets the connection from the HikariCP (connection pool).
 * <p>
 * To enable HikariCP, you have to add to jdbc.properties the following properties:
 * <pre>
 * session.class=com.miragesql.miragesql.session.HikariCPSessionImpl
 * jdbc.driver=com.mysql.jdbc.Driver
 * jdbc.url=jdbc:mysql://localhost/test
 * jdbc.user=root
 * jdbc.password=
 * dataSource.setting_X=value_X
 * dataSource.setting_Y=value_Y
 * </pre>
 *
 * where <code>setting_X</code> and <code>setting_Y</code> are HikariCP specific <b>optional</b> properties.
 * Note: All HikariCP optional properties must be prefixed with <code>dataSource.</code>.
 * See <a href="https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby" target="_blank">https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby</a> for details.
 */
public class HikariCPSessionImpl implements Session {

    private static final Logger logger = LoggerFactory.getLogger(HikariCPSessionImpl.class);

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
     *     <li>dataSource.xxx - HikariCP Optional settings</li>
     *   </ul>
     */
    public HikariCPSessionImpl(Properties properties){
        String driver   = properties.getProperty("jdbc.driver");
        String url      = properties.getProperty("jdbc.url");
        String user     = properties.getProperty("jdbc.user");
        String password = properties.getProperty("jdbc.password");


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

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);

        // set Hikari optional properties. All start with "dataSource.XXX"
        PropertyElf.setTargetFromProperties(config, properties);

        dataSource = new HikariDataSource(config);
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
