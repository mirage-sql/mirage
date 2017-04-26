package com.miragesql.miragesql;

import java.io.IOException;
import java.io.InputStream;


public interface SqlResource {
	
	InputStream getInputStream() throws IOException;
	
}
