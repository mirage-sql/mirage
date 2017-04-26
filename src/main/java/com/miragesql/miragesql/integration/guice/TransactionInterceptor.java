package com.miragesql.miragesql.integration.guice;

import java.util.logging.Logger;

import com.miragesql.miragesql.session.SessionFactory;
import com.miragesql.miragesql.util.ExceptionUtil;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * An interceptor to control transaction.
 *
 * @author Naoki Takezoe
 * @see Transactional
 */
public class TransactionInterceptor implements MethodInterceptor {

	private static final Logger logger = Logger.getLogger(TransactionInterceptor.class.getName());

	public Object invoke(MethodInvocation invocation) throws Throwable {

		try {
			SessionFactory.getSession().begin();
		} catch(Exception ex){
			logger.severe("Failed to begin Session.");
			logger.severe(ExceptionUtil.toString(ex));
			throw ex;
		}

		try {
			Object result = invocation.proceed();
			try {
				SessionFactory.getSession().commit();

			} catch(Exception ex){
				logger.severe("Failed to commit Session.");
				logger.severe(ExceptionUtil.toString(ex));
				throw ex;
			}

			return result;

		} catch(Exception ex){
			try {
				SessionFactory.getSession().rollback();

			} catch(Exception e){
				logger.severe("Failed to rollback Session.");
				logger.severe(ExceptionUtil.toString(e));
				throw e;
			}
			throw ex;

		} finally {
			try {
				SessionFactory.getSession().release();

			} catch (Exception ex) {
				logger.severe("Failed to release Session.");
				logger.severe(ExceptionUtil.toString(ex));
				throw ex;
			}
		}
	}

}
