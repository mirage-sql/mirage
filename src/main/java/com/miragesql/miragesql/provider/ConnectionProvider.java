package jp.sf.amateras.mirage.provider;

import java.sql.Connection;

import jp.sf.amateras.mirage.SqlManager;
import jp.sf.amateras.mirage.exception.SQLRuntimeException;

/**
 * The interface of the connection provider.
 * <p>
 * Mirage uses this interface to get the connection to the database.
 *
 * @author Naoki Takezoe
 * @see SqlManager#setConnectionProvider(ConnectionProvider)
 */
public interface ConnectionProvider {

	/**
	 * Returns the connection to the database.
	 *
	 * @return the connection
	 * @throws SQLRuntimeException Failed to get the connection
	 */
	public Connection getConnection();

}
