package jp.sf.amateras.mirage.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jp.sf.amateras.mirage.exception.IORuntimeException;

public class IOUtil {

	/**
	 * 
	 * @param in
	 * @return
	 * @throws IORuntimeException if some other I/O error occurs.
	 */
	public static byte[] readStream(InputStream in) {
		if(in == null){
			return null;
		}
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024 * 8];
			int length = 0;
			while((length = in.read(buf)) != -1){
				out.write(buf, 0, length);
			}
			return out.toByteArray();

		} catch (IOException e) {
			throw new IORuntimeException(e);
			
		} finally {
			closeQuietly(in);
			closeQuietly(out);
		}
	}

	public static void closeQuietly(Closeable closeable){
		if(closeable != null){
			try {
				closeable.close();
			} catch (Exception ex){
				// ignore
			}
		}
	}

	public static Properties loadProperties(String path){
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream in = null;
		try {
			in = cl.getResourceAsStream(path);

			Properties properties = new Properties();
			properties.load(in);

			return properties;

		} catch(IOException ex){
			return null;

		} finally {
			IOUtil.closeQuietly(in);
		}
	}

}
