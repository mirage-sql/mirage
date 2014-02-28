package jp.sf.amateras.mirage.session;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import jp.sf.amateras.mirage.exception.ConfigurationException;
import jp.sf.amateras.mirage.util.IOUtil;
import jp.sf.amateras.mirage.util.StringUtil;

public class SessionFactory {

	private static Session session;

	/**
	 * 
	 * @return {@link Session}
	 * @throws ConfigurationException
	 */
	public synchronized static Session getSession() {
		if(session == null){
			Properties properties = IOUtil.loadProperties("jdbc.properties");
			getSession(properties);
		}
		
		return session;
	}

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
				
			} catch (InstantiationException e) {
				throw new ConfigurationException(e);
				
			} catch (IllegalAccessException e) {
				throw new ConfigurationException(e);
				
			} catch (InvocationTargetException e) {
				throw new ConfigurationException(e);
				
			} catch (SecurityException e) {
				throw new ConfigurationException(e);
				
			}
		}

		return session;
	}

}
