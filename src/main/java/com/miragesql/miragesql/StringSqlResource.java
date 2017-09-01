package com.miragesql.miragesql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link SqlResource} represented by an SQL string.
 */
public class StringSqlResource implements SqlResource {

    /** The SQL content. */
    private String sql;

    /**
     * Constructs the {@link SqlResource}.
     *
     * @param sql string representing an SQL
     */
    public StringSqlResource(final String sql) {
        this.sql = sql;
    }

    /**{@inheritDoc}*/
    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(sql.getBytes("UTF-8"));
    }

    /**{@inheritDoc}*/
    @Override
    public String toString() {
        return "StringSqlResource [sql=" + sql + "]";
    }

    /**{@inheritDoc}*/
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sql == null) ? 0 : sql.hashCode());
        return result;
    }

    /**{@inheritDoc}*/
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
