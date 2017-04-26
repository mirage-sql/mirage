package com.miragesql.miragesql.updater;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.miragesql.miragesql.SqlManagerImpl;
import com.miragesql.miragesql.session.Session;
import com.miragesql.miragesql.session.SessionFactory;
import com.miragesql.miragesql.util.ExceptionUtil;
import com.miragesql.miragesql.util.StringUtil;

/**
 * This is a ServletContextListener to execute {@link SchemaUpdater} when the servlet context is initialized.
 * <p>
 * This listener is only available when Mirage is working standalone.
 * Because this listener get connection configurations from jdbc.properties using {@link SessionFactory}.
 * If you want to use Mirage with DI containers such as Spring or etc, you can't use this listener.
 * </p>
 *
 * @author Naoki Takezoe
 */
public class SchemaUpdateListener implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(SchemaUpdateListener.class.getName());

	public void contextInitialized(ServletContextEvent sce) {

		SchemaUpdater updater = new SchemaUpdater();
		Session session = SessionFactory.getSession();

		updater.setSqlManager(session.getSqlManager());

		String packageName =
			sce.getServletContext().getInitParameter("SCHEMA_UPDATE_SQL_PACKAGE");

		if(StringUtil.isNotEmpty(packageName)){
			updater.setPackageName(packageName);
		}

		try {
			session.begin();

			Connection conn = ((SqlManagerImpl) session.getSqlManager())
				.getConnectionProvider().getConnection();

			try {
				conn.setAutoCommit(true);


			} catch (SQLException ex){
				logger.severe("Failed to update schema.");
				logger.severe(ExceptionUtil.toString(ex));

			} finally {
				try {
					conn.setAutoCommit(false);
				} catch (SQLException ex) {
					logger.severe(ExceptionUtil.toString(ex));
				}
			}
		} finally {
			session.release();
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
	}

}
