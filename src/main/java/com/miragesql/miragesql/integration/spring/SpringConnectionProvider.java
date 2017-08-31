package com.miragesql.miragesql.integration.spring;

import java.sql.Connection;

import javax.sql.DataSource;

import com.miragesql.miragesql.provider.ConnectionProvider;

import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * {@link ConnectionProvider} implementation to integrate Mirage-SQL with Spring Framework.
 *
 * @author Naoki Takezoe
 */
public class SpringConnectionProvider implements ConnectionProvider {

    private DataSource dataSource;

    public void setTransactionManager(
            DataSourceTransactionManager transactionManager) {
        dataSource = transactionManager.getDataSource();
    }

    public void setDataSource(DataSource dataSource) {
        if (dataSource instanceof TransactionAwareDataSourceProxy) {
            // If we got a TransactionAwareDataSourceProxy, we need to perform transactions
            // for its underlying target DataSource, else data access code won't see
            // properly exposed transactions (i.e. transactions for the target DataSource).
            this.dataSource = ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource();
        }
        else {
            this.dataSource = dataSource;
        }
    }

    public Connection getConnection() {
        ConnectionHolder conHolder =
            (ConnectionHolder) TransactionSynchronizationManager.getResource(
            dataSource);

        if (conHolder == null) {
            throw new IllegalStateException("It seems not to be existing a transaction.");
        }

        return conHolder.getConnection();
    }

}
