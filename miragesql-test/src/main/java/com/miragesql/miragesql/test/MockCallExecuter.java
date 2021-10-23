package com.miragesql.miragesql.test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.miragesql.miragesql.CallExecutor;
import com.miragesql.miragesql.exception.SQLRuntimeException;

/**
 * The mock class of {@link CallExecutor}.
 *
 * @author Naoki Takezoe
 * @see MockSqlManager
 * @see MirageTestContext
 */
public class MockCallExecuter extends CallExecutor {

    /**{@inheritDoc}*/
    @Override
    public void call(String sql, Object parameter){
        try {
            List<Param> paramList = new ArrayList<>();
            List<Param> nonParamList = new ArrayList<>();

            prepareParameters(paramList, nonParamList, null, parameter);

            List<Object> params = new ArrayList<>();
            for(Param param: paramList){
                params.add(param.value);
            }
            MirageTestContext.addExecutedSql(new ExecutedSQLInfo(sql, params.toArray()));

        } catch(SQLException ex){
            throw new SQLRuntimeException(ex);
        }
    }

    /**{@inheritDoc}*/
    @Override
    @SuppressWarnings("unchecked")
    public <T> T call(Class<T> resultClass, String sql, Object parameter){
        try {
            List<Param> paramList = new ArrayList<>();
            List<Param> nonParamList = new ArrayList<>();

            prepareParameters(paramList, nonParamList, null, parameter);

            List<Object> params = new ArrayList<>();
            for(Param param: paramList){
                params.add(param.value);
            }
            MirageTestContext.addExecutedSql(new ExecutedSQLInfo(sql, params.toArray()));

            if(MirageTestContext.hasNextResult()){
                return (T) MirageTestContext.getNextResult();
            }
            return null;

        } catch(SQLException ex){
            throw new SQLRuntimeException(ex);
        }
    }

    /**{@inheritDoc}*/
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> callForList(Class<T> resultClass, String sql, Object parameter){
        try {
            List<Param> paramList = new ArrayList<>();
            List<Param> nonParamList = new ArrayList<>();

            prepareParameters(paramList, nonParamList, null, parameter);

            List<Object> params = new ArrayList<>();
            for(Param param: paramList){
                params.add(param.value);
            }
            MirageTestContext.addExecutedSql(new ExecutedSQLInfo(sql, params.toArray()));

            if(MirageTestContext.hasNextResult()){
                return (List<T>) MirageTestContext.getNextResult();
            }
            return null;

        } catch(SQLException ex){
            throw new SQLRuntimeException(ex);
        }
    }


}
