package com.miragesql.miragesql;

import java.io.IOException;
import java.io.InputStream;

/**
 * SqlResource is a common interface to access SQLs.
 */
public interface SqlResource {

    /**
     * Retrieves the Input Stream associated with a given SQL.
     *
     * @return an {@link InputStream}
     * @throws IOException if something goes wrong trying to access the SQL.
     */
    InputStream getInputStream() throws IOException;
}
