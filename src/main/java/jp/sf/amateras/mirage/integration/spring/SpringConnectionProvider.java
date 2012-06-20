package jp.sf.amateras.mirage.integration.spring;

import java.sql.Connection;

import jp.sf.amateras.mirage.provider.ConnectionProvider;

import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * {@link ConnectionProvider} implementation to integrate Mirage to Spring Framework.
 *
 * @author Naoki Takezoe
 */
public class SpringConnectionProvider implements ConnectionProvider {

	private DataSourceTransactionManager transactionManager;

	public void setTransactionManager(
			DataSourceTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public Connection getConnection() {
		ConnectionHolder conHolder =
			(ConnectionHolder) TransactionSynchronizationManager.getResource(
			transactionManager.getDataSource());

		if (conHolder == null) {
			throw new IllegalStateException("It seems not to be existing a transaction.");
		}

		return conHolder.getConnection();
	}

}
