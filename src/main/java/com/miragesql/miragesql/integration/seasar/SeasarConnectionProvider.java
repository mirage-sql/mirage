package com.miragesql.miragesql.integration.seasar;

import java.sql.Connection;

import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;

import com.miragesql.miragesql.provider.ConnectionProvider;
import com.miragesql.miragesql.provider.XADataSourceConnectionProvider;

/**
 * {@link ConnectionProvider} implementation to use Mirage-SQL with Seasar2.
 *
 * @author Naoki Takezoe
 */
public class SeasarConnectionProvider extends XADataSourceConnectionProvider {

	private ThreadLocal<Boolean> registered= new ThreadLocal<>();
	private TransactionSynchronizationRegistry syncRegistry;

	public void setTransactionSynchronizationRegistry(TransactionSynchronizationRegistry syncRegistry) {
		this.syncRegistry = syncRegistry;
	}

	@Override
	public Connection getConnection() {
		// If TransactionSynchronizationRegistry exists,
		// register Synchronization to release connection automatically
		// at the first invocation of this method in the current thread.
		// TODO: I wonder if I can register for each thread ...
		if(syncRegistry != null && registered.get() == null){
			syncRegistry.registerInterposedSynchronization(new Synchronization() {
				//@Override
				public void beforeCompletion() {
				}

				//@Override
				public void afterCompletion(int status) {
					releaseConnection();
					registered.remove();
				}
			});
			registered.set(true);
		}

		return super.getConnection();
	}

}
