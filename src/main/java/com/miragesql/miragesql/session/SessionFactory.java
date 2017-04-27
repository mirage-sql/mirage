package com.miragesql.miragesql.session;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import com.miragesql.miragesql.exception.ConfigurationException;
import com.miragesql.miragesql.util.IOUtil;
import com.miragesql.miragesql.util.StringUtil;

public class SessionFactory {

	private static Session session;

	/**
	 * Returns a session configured with the properties specified in the "jdbc.properties" file.
     *
	 * @return {@link Session}
     *
	 * @throws ConfigurationException if the configuration can't be loaded
	 */
	public synchronized static Session getSession() {
		if(session == null){
			Properties properties = IOUtil.loadProperties("jdbc.properties");
			session = getSession(properties);
		}
		
		return session;
	}

    /**
     * Returns a session configured with the properties specified by the properties parameter.
     * If no <code>session.class</code> key is present in the properties, the session will use the {@link JDBCSessionImpl}.
     *
     * @param properties properties with settings to create a {@link Session} with.
     *
     * @return {@link Session}
     */
	public synchronized static Session getSession(Properties properties) {
		if(session == null){
			if(properties == null){
				throw new ConfigurationException("jdbc.properties is not found!");
			}
			
			try {
				String sessionClass = properties.getProperty("session.class");
				if(StringUtil.isEmpty(sessionClass)){
					sessionClass = JDBCSessionImpl.class.getName();
				}

				Class<?> clazz = Class.forName(sessionClass);
				Constructor<?> constructor = clazz.getConstructor(Properties.class);
				session = (Session) constructor.newInstance(properties);

			} catch (ClassNotFoundException e) {
				throw new ConfigurationException("Driver class not found.", e);
				
			} catch (NoSuchMethodException e) {
				throw new ConfigurationException(
						"sessionClass does not have constructor with argument type of Properties.", e);
				
			} catch (ClassCastException e) {
				throw new ConfigurationException("sessionClass does not implements Session interface ", e);
				
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException e) {
				throw new ConfigurationException(e);
			}
        }

		return session;
	}

}
