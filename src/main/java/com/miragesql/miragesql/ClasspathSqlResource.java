package com.miragesql.miragesql;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link SqlResource} represented by an sql path (of the file containing the SQL.
 */
public class ClasspathSqlResource implements SqlResource {

    /** The path to the file containing the SQL. */
    private final String sqlPath;

    /**
     * Constructs a {@link SqlResource} from a file path.
     *
     * @param sqlPath the SQL file path.
     */
    public ClasspathSqlResource(final String sqlPath) {
        this.sqlPath = sqlPath;
    }

    /**{@inheritDoc}*/
    @Override
    public InputStream getInputStream() throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return cl.getResourceAsStream(sqlPath);
    }

    /**{@inheritDoc}*/
    @Override
    public String toString() {
        return "ClasspathSqlResource [sqlPath=" + sqlPath + "]";
    }

    /**{@inheritDoc}*/
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sqlPath == null) ? 0 : sqlPath.hashCode());
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
        ClasspathSqlResource other = (ClasspathSqlResource) obj;
        if (sqlPath == null) {
            if (other.sqlPath != null)
                return false;
        } else if (!sqlPath.equals(other.sqlPath))
            return false;
        return true;
    }
}
