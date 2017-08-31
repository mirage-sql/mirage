package com.miragesql.miragesql.provider;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.XADataSource;

/**
 * {@link ConnectionProvider} implementation which gets a connection from <code>javax.sql.XADataSource</code>
 * which is obtained from JNDI.
 *
 * @author Naoki Takezoe
 */
public class JNDIXADataSourceConnectionProvider extends XADataSourceConnectionProvider {

    public JNDIXADataSourceConnectionProvider(String jndiName) throws NamingException {
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:/comp/env");
        XADataSource xaDataSource = (XADataSource) envContext.lookup(jndiName);
        setDataSource(xaDataSource);
    }

}
