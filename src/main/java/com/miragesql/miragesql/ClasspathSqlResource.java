package com.miragesql.miragesql;

import java.io.IOException;
import java.io.InputStream;


public class ClasspathSqlResource implements SqlResource {
	
	private String sqlPath;

	public ClasspathSqlResource(String sqlPath) {
		this.sqlPath = sqlPath;
	}
	
	public InputStream getInputStream() throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream in = cl.getResourceAsStream(sqlPath);
		return in;
	}

	@Override
	public String toString() {
		return "ClasspathSqlResource [sqlPath=" + sqlPath + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sqlPath == null) ? 0 : sqlPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClasspathSqlResource other = (ClasspathSqlResource) obj;
		if (sqlPath == null) {
			if (other.sqlPath != null)
				return false;
		} else if (!sqlPath.equals(other.sqlPath))
			return false;
		return true;
	}
}
