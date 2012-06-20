package jp.sf.amateras.mirage.provider;

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
		Context context = new InitialContext();
		XADataSource xaDataSource = (XADataSource) context.lookup(jndiName);
		setDataSource(xaDataSource);
	}

}
