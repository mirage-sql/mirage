package jp.sf.amateras.mirage.exception;

import java.sql.SQLException;

public class SQLRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SQLRuntimeException(String message, SQLException cause) {
		super(message, cause);
	}

	public SQLRuntimeException(SQLException cause) {
		super(cause);
	}
	
	@Override
	public SQLException getCause() {
		return (SQLException) super.getCause();
	}
}
