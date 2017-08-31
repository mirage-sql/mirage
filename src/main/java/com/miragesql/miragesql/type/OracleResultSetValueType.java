package com.miragesql.miragesql.type;


public class OracleResultSetValueType extends AbstractResultSetValueType {

    public static final int CURSOR = -10;

    public OracleResultSetValueType() {
        super(CURSOR);
    }

}
