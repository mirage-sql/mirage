package com.miragesql.miragesql.dialect;

public class SQLiteDialect extends StandardDialect {

    @Override /**{@inheritDoc}**/
    public String getName() {
        return "sqlite";
    }

}
