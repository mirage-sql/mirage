package com.miragesql.miragesql.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    /**
     * Returns stacktrace as string.
     *
     * @param ex the exception
     * @return stacktrace
     */
    public static String toString(Exception ex){
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);

        ex.printStackTrace(pw);

        return writer.toString();
    }

}
