package com.miragesql.miragesql.dialect;

public class HyperSQLDialect extends StandardDialect {

    /**{@inheritDoc}**/
    @Override
    public String getName() {
        return "hsqldb";
    }

    /**{@inheritDoc}**/
    @Override
    public String getSequenceSql(String sequenceName) {
        return String.format("SELECT NEXT VALUE FOR %s " +
                "FROM INFORMATION_SCHEMA.SYSTEM_TABLES " +
                "WHERE table_name = 'SYSTEM_TABLES'", sequenceName);
    }

}
