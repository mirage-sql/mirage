package jp.sf.amateras.mirage.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Provides utility methods for JDBC operation.
 *
 * @author Naoki Takezoe
 */
public class JdbcUtil {

	/**
	 * Closes the <code>Connection</code> with no exceptions.
	 *
	 * @param conn the <code>Connection</code> to close
	 */
	public static void close(Connection conn){
		if(conn != null){
			try {
				conn.close();
			} catch (Exception ex){
				// ignore
			}
		}
	}

	/**
	 * Closes the <code>Statement</code> with no exceptions.
	 *
	 * @param stmt the <code>Statement</code> to close
	 */
	public static void close(Statement stmt){
		if(stmt != null){
			try {
				stmt.close();
			} catch (Exception ex){
				// ignore
			}
		}
	}

	/**
	 * Closes the <code>ResultSet</code> with no exceptions.
	 *
	 * @param rs the <code>ResultSet</code> to close
	 */
	public static void close(ResultSet rs){
		if(rs != null){
			try {
				rs.close();
			} catch (Exception ex){
				// ignore
			}
		}
	}
}
