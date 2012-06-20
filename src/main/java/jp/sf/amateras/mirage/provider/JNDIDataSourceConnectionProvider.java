package jp.sf.amateras.mirage.provider;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * {@link ConnectionProvider} implementation which gets a connection from <code>javax.sql.DataSource</code>
 * which is obtained from JNDI.
 *
 * @author Naoki Takezoe
 */
public class JNDIDataSourceConnectionProvider extends DataSourceConnectionProvider {

	public JNDIDataSourceConnectionProvider(String jndiName) throws NamingException {
		Context context = new InitialContext();
		DataSource dataSource = (DataSource) context.lookup(jndiName);
		setDataSource(dataSource);
	}

}
