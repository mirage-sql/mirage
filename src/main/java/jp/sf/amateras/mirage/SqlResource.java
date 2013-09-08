package jp.sf.amateras.mirage;

import java.io.IOException;
import java.io.InputStream;


public interface SqlResource {
	
	InputStream getInputStream() throws IOException;
	
}
