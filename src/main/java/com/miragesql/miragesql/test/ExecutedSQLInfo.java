package com.miragesql.miragesql.test;

import com.miragesql.miragesql.bean.PropertyDesc;

/**
 *
 * @author Naoki Takezoe
 */
public class ExecutedSQLInfo {

    private String sql;
    private PropertyDesc[] propDescs;
    private Object entity;
    private Object[] params;

    /**
     * Constructor.
     *
     * @param sql the executed SQL
     * @param propDescs the array of bound parameters
     * @param entity the entity
     */
    public ExecutedSQLInfo(String sql, PropertyDesc[] propDescs, Object entity){
        this.sql = sql;
        this.propDescs = propDescs;
        this.entity = entity;
    }

    /**
     * Constructor.
     *
     * @param sql the executed SQL
     * @param params the array of bound parameters
     */
    public ExecutedSQLInfo(String sql, Object[] params){
        this.sql = sql;
        this.params = params;
    }

    /**
     * Returns the executed SQL.
     *
     * @return the executed SQL
     */
    public String getSql(){
        return this.sql;
    }

    /**
     * Returns the array of bound parameters.
     *
     * @return the array of bound parameters
     */
    public Object[] getParams() {
        if(params == null) {
            params = new Object[propDescs.length];
            for (int i = 0; i < propDescs.length; i++) {
                params[i] = propDescs[i].getValue(entity);
            }
        }
        return params;
    }

}
