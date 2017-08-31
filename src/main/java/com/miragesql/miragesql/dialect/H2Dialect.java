package com.miragesql.miragesql.dialect;

public class H2Dialect extends StandardDialect {

    /**{@inheritDoc}**/
    @Override
    public String getName() {
        return "h2";
    }

    /**{@inheritDoc}**/
    @Override
    public String getSequenceSql(String sequenceName) {
        return String.format("SELECT NEXTVAL('%s')", sequenceName);
    }

}
