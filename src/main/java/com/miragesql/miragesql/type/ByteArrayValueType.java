package com.miragesql.miragesql.type;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.miragesql.miragesql.bean.PropertyDesc;
import com.miragesql.miragesql.util.IOUtil;

public class ByteArrayValueType extends AbstractValueType<byte[]> {

    public static void main(String[] args) {
        Class<?> clazz = byte[].class;
        System.out.println(clazz.isArray());
        System.out.println(clazz.getComponentType() == Byte.TYPE);
    }

    public ByteArrayValueType() {
        super(byte[].class);
    }

    @Override
    public boolean isSupport(Class<?> type, PropertyDesc propertyDesc) {
        return type.isArray() && type.getComponentType() == Byte.TYPE;
    }

    public byte[] get(Class<? extends byte[]> type, ResultSet rs, int columnIndex) throws SQLException {
        if(rs.getObject(columnIndex) == null){
            return null;
        }
        return IOUtil.readStream(rs.getBinaryStream(columnIndex));
    }

    public byte[] get(Class<? extends byte[]> type, ResultSet rs, String columnName) throws SQLException {
//		if(type.isArray() && type.getComponentType() == Byte.TYPE){
        if(rs.getObject(columnName) == null){
            return null;
        }
        return IOUtil.readStream(rs.getBinaryStream(columnName));
    }

    public void set(Class<? extends byte[]> type, PreparedStatement stmt, byte[] value,
            int index) throws SQLException {
        if (value == null){
            setNull(type, stmt, index);
        } else {
            stmt.setBinaryStream(index, new ByteArrayInputStream(value), value.length);
        }
    }

    public byte[] get(Class<? extends byte[]> type, CallableStatement cs, int index) throws SQLException {
        // TODO
        Blob blob = cs.getBlob(index);
        return IOUtil.readStream(blob.getBinaryStream());
    }

    public byte[] get(Class<? extends byte[]> type, CallableStatement cs, String parameterName) throws SQLException {
        Blob blob = cs.getBlob(parameterName);
        return IOUtil.readStream(blob.getBinaryStream());
    }
}
