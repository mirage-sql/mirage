package jp.sf.amateras.mirage.exception;

public class OgnlRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OgnlRuntimeException(Throwable cause, String path, int lineNumber) {
		super("OGNL error in path: " + path + ", line:" + lineNumber, cause);
	}

}
