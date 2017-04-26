package jp.sf.amateras.mirage.exception;

import java.io.IOException;


@SuppressWarnings("serial")
public class IORuntimeException extends RuntimeException {

	public IORuntimeException(String message, IOException cause) {
		super(message, cause);
	}

	public IORuntimeException(IOException cause) {
		super(cause);
	}
	
	@Override
	public IOException getCause() {
		return (IOException) super.getCause();
	}
}
