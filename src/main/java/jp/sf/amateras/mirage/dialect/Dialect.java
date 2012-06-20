package jp.sf.amateras.mirage.dialect;

import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import jp.sf.amateras.mirage.type.ValueType;

public interface Dialect {

    /**
     * Returns the dialect name.
     *
     * @return the dialect name
     */
    String getName();

    /**
     * Returns whether the result set of procedure invocation requires parameter or not.
     *
     * @return true then rqeuired, faise then not required
     */
    boolean needsParameterForResultSet();

    boolean supportsGenerationType(GenerationType generationType);

    String getSequenceSql(String sequenceName);

    String getCountSql(String sql);

    /**
     * Returnes the {@link ValueType} for the database product.
     *
     * @return the value type or null
     */
    public ValueType<?> getValueType();

}
