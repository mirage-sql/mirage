package com.miragesql.miragesql.session;

import com.miragesql.miragesql.SqlManager;
import com.miragesql.miragesql.exception.SessionException;
import com.miragesql.miragesql.filter.OpenSessionInViewFilter;

/**
 * The entry point of Mirage in stand-alone use.
 * <p>
 * You can control transaction and access {@link SqlManager} through this interface.
 * <p>
 * In addition, you can manage transaction automatically using this interface with {@link OpenSessionInViewFilter}.
 * <code>OpenSessionInViewFilter</code> begin and commit transaction per request.
 * It rollback transaction if it catches exception. So you can focus on operation of <code>SqlManager</code>.
 *
 * @author Naoki Takezoe
 */
public interface Session {

	/**
	 * Begins the transaction.
	 *
	 * @throws SessionException
	 */
	public void begin();

	/**
	 * Commits the transaction.
	 *
	 * @throws SessionException
	 */
	public void commit();

	/**
	 * Roolbacks the transaction.
	 *
	 * @throws SessionException
	 */
	public void rollback();

	/**
	 * Releases this session.
	 *
	 * @throws SessionException
	 */
	public void release();

	/**
	 * Marks the current transaction as rollback-only.
	 */
	public void setRollbackOnly();

	/**
	 * Returns whether the current transaction has been marked as rollback-only or not marked.
	 *
	 * @return If marked true; otherwise false
	 */
	public boolean isRollbackOnly();

	/**
	 * Returns the instance of <code>SqlManager</code>.
	 *
	 * @return the instance of <code>SqlManager</code>
	 * @throws SessionException
	 */
	public SqlManager getSqlManager();

}
