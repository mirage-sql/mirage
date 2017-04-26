package jp.sf.amateras.mirage.session;

import jp.sf.amateras.mirage.SqlManager;
import jp.sf.amateras.mirage.SqlManagerImpl;
import jp.sf.amateras.mirage.exception.ConfigurationException;
import jp.sf.amateras.mirage.exception.SessionException;
import jp.sf.amateras.mirage.provider.DataSourceConnectionProvider;
import jp.sf.amateras.mirage.provider.DefaultConnectionProvider;
import jp.sf.amateras.mirage.provider.JNDIDataSourceConnectionProvider;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JNDI based (Mirage) Session implementation.
 */
public class JNDISessionImpl implements Session {

    private static final Logger logger = Logger.getLogger(JNDISessionImpl.class.getName());

    private SqlManager sqlManager;
    JNDIDataSourceConnectionProvider provider;
    private ThreadLocal<Boolean> rollbackOnly = new ThreadLocal<Boolean>();

    /**
     * The implementation of {@link Session} which gets the connection from a JNDI Data Source.
     * <p/>
     * To enable JNDI you need to add the following properties to <code>jdbc.properties</code>:
     * <pre>
     *  session.class=jp.sf.amateras.mirage.session.JNDISessionImpl
     *  jndi.name=jdbc/mydbname
     *  db.dialect=jdbc:mysql:
     *  sql.cache=true
     * </pre>
     */
    public JNDISessionImpl(Properties properties) {
        String jndiName = properties.getProperty("jndi.name");
        String dialectUrl = properties.getProperty("db.dialect");

        sqlManager = new SqlManagerImpl();
        sqlManager.setDialect(DialectAutoSelector.getDialect(dialectUrl));

        try {
            provider = new JNDIDataSourceConnectionProvider(jndiName);
        } catch (NamingException e) {
            throw new ConfigurationException(e);
        }

        sqlManager.setConnectionProvider(provider);

        String cache = properties.getProperty("sql.cache");
        if("true".equals(cache)){
            ((SqlManagerImpl) sqlManager).setCacheMode(true);
        } else {
            ((SqlManagerImpl) sqlManager).setCacheMode(false);
        }
    }

    /** {@inheritDoc}*/
    public void begin() {
        if(logger.isLoggable(Level.INFO)){
            logger.info("Begin transaction.");
        }
        try {
            provider.getConnection();
            Connection conn = provider.getConnection();
            conn.setAutoCommit(false);
            // provider.setConnection(conn);
        } catch (SQLException ex){
            throw new SessionException("Failed to begin transaction.", ex);
        }
    }

    /** {@inheritDoc}*/
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

    /** {@inheritDoc}*/
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

    /** {@inheritDoc}*/
    public void release() {
        this.rollbackOnly.remove();
        if(provider instanceof DataSourceConnectionProvider){
            ((DataSourceConnectionProvider) provider).releaseConnection();
        }
    }

    /** {@inheritDoc}*/
    public void setRollbackOnly() {
        this.rollbackOnly.set(true);
    }

    /** {@inheritDoc}*/
    public boolean isRollbackOnly() {
        return this.rollbackOnly.get() != null;
    }

    /** {@inheritDoc}*/
    public SqlManager getSqlManager() {
        return sqlManager;
    }
}
