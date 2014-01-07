package jp.sf.amateras.mirage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringSqlResource implements SqlResource {
	
	private String sql;

	public StringSqlResource(String sql) {
		this.sql = sql;
	}
	
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(sql.getBytes("UTF-8"));
	}

	@Override
	public String toString() {
		return "StringSqlResource [sql=" + sql + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sql == null) ? 0 : sql.hashCode());
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
		StringSqlResource other = (StringSqlResource) obj;
		if (sql == null) {
			if (other.sql != null)
				return false;
		} else if (!sql.equals(other.sql))
			return false;
		return true;
	}
}
