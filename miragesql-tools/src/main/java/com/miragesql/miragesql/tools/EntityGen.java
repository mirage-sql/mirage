package com.miragesql.miragesql.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.miragesql.miragesql.annotation.Column;
import com.miragesql.miragesql.annotation.PrimaryKey;
import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import com.miragesql.miragesql.annotation.Table;
import com.miragesql.miragesql.dialect.Dialect;
import com.miragesql.miragesql.naming.NameConverter;
import com.miragesql.miragesql.type.BigDecimalValueType;
import com.miragesql.miragesql.type.BooleanPrimitiveValueType;
import com.miragesql.miragesql.type.BooleanValueType;
import com.miragesql.miragesql.type.ByteArrayValueType;
import com.miragesql.miragesql.type.DoublePrimitiveValueType;
import com.miragesql.miragesql.type.DoubleValueType;
import com.miragesql.miragesql.type.FloatPrimitiveValueType;
import com.miragesql.miragesql.type.FloatValueType;
import com.miragesql.miragesql.type.IntegerPrimitiveValueType;
import com.miragesql.miragesql.type.IntegerValueType;
import com.miragesql.miragesql.type.LongPrimitiveValueType;
import com.miragesql.miragesql.type.LongValueType;
import com.miragesql.miragesql.type.ShortPrimitiveValueType;
import com.miragesql.miragesql.type.ShortValueType;
import com.miragesql.miragesql.type.SqlDateValueType;
import com.miragesql.miragesql.type.StringValueType;
import com.miragesql.miragesql.type.TimeValueType;
import com.miragesql.miragesql.type.TimestampValueType;
import com.miragesql.miragesql.type.UtilDateValueType;
import com.miragesql.miragesql.type.ValueType;
import com.miragesql.miragesql.util.JdbcUtil;
import com.miragesql.miragesql.util.StringUtil;


/**
 * Entity Generation Tool.
 * <pre> // setup
 * EntityGen gen = new EntityGen();
 * gen.setPackageName("com.miragesql.miragesql.entity");
 * gen.setNameConverter(new DefaultNameConverter());
 * gen.setDialect(new StandardDialect());
 *
 * // generate
 * String source = gen.getEntitySource(conn, "BOOK", null, null); </pre>
 * <ul>
 *   <li>TODO - Generate Javadoc from DB Comments</li>
 * </ul>
 * @author Naoki Takezoe
 */
public class EntityGen {

    private String packageName;
    private NameConverter nameConverter;
    private List<ValueType<?>> valueTypes = new ArrayList<>();
    private Dialect dialect;
    private GenerationType generationType;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public EntityGen(){
        addValueType(new StringValueType());
        addValueType(new IntegerValueType());
        addValueType(new IntegerPrimitiveValueType());
        addValueType(new LongValueType());
        addValueType(new LongPrimitiveValueType());
        addValueType(new ShortValueType());
        addValueType(new ShortPrimitiveValueType());
        addValueType(new DoubleValueType());
        addValueType(new DoublePrimitiveValueType());
        addValueType(new FloatValueType());
        addValueType(new FloatPrimitiveValueType());
        addValueType(new BooleanValueType());
        addValueType(new BooleanPrimitiveValueType());
        addValueType(new BigDecimalValueType());
        addValueType(new SqlDateValueType());
        addValueType(new UtilDateValueType());
        addValueType(new TimeValueType());
        addValueType(new TimestampValueType());
        addValueType(new ByteArrayValueType());
//		addValueType(new com.miragesql.miragesql.type.DefaultValueType());
    }

    public void setDialect(Dialect dialect){
        this.dialect = dialect;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setNameConverter(NameConverter nameConverter) {
        this.nameConverter = nameConverter;
    }

    public void addValueType(ValueType<?> valueType){
        this.valueTypes.add(valueType);
    }

    public void setGenerationType(GenerationType generationType) {
        this.generationType = generationType;
    }

    /**
     * Returns the source code of the entity class which corresponds to the specified table.
     *
     * @param conn the DB connection
     * @param tableName the DB table name
     * @param catalog the DB catalog
     * @param schema the DB schema
     *
     * @return the source
     *
     * @throws SQLException if a something goes wrong when working with the database
     *
     * @deprecated use {@link #getEntitySource(Connection, String, String, String, String)}
     */
    public String getEntitySource(Connection conn,
            String tableName, String catalog, String schema) throws SQLException {
        return getJavaEntitySource(conn, tableName, catalog, schema);
    }

    /**
     * Returns the source code of the entity class which corresponds to the specified table.
     *
     * @param conn the DB connection
     * @param tableName the DB table name
     * @param catalog the DB catalog
     * @param schema the DB schema
     * @param lang the language to generate to. Supported are 'java' and 'groovy'
     *
     * @return the source
     *
     * @throws SQLException if a something goes wrong when working with the database
     */
    public String getEntitySource(Connection conn,
                                  String tableName, String catalog, String schema, String lang) throws SQLException {
        validateLang(lang);
        if("groovy".equals(lang)){
            return getGroovyEntitySource(conn, tableName, catalog, schema);
        } else if("xml".equals(lang)){
            return getXmlEntitySource(conn, tableName, catalog, schema);
        }
        return getJavaEntitySource(conn, tableName, catalog, schema);
    }

    /**
     * Returns the Java source code of the entity class which corresponds to the specified table.
     *
     * @param conn the DB connection
     * @param tableName the DB table name
     * @param catalog the DB catalog
     * @param schema the DB schema
     *
     * @return the source
     *
     * @throws SQLException if a something goes wrong when working with the database
     */
    protected String getJavaEntitySource(Connection conn,
                                  String tableName, String catalog, String schema) throws SQLException {

        StringBuilder sb = new StringBuilder();

        // package declaration
        sb.append("package ").append(packageName).append(";").append(LINE_SEPARATOR);
        sb.append(LINE_SEPARATOR);

        // import statements
        appendImport(sb, Table.class);
        appendImport(sb, Column.class);
        appendImport(sb, PrimaryKey.class);
        appendImport(sb, GenerationType.class);
        sb.append(LINE_SEPARATOR);

        // class declaration
        sb.append("@Table(name=\"").append(tableName).append("\")").append(LINE_SEPARATOR);
        sb.append("public class ").append(tableToEntity(tableName)).append(" {").append(LINE_SEPARATOR);
        sb.append(LINE_SEPARATOR);

        // Entity properties
        DatabaseMetaData meta = conn.getMetaData();
        List<String> primaryKeys = new ArrayList<>();

        ResultSet keys = meta.getPrimaryKeys(catalog, schema, tableName);
        while(keys.next()){
            String columnName = keys.getString("COLUMN_NAME");
            primaryKeys.add(columnName);
        }
        keys.close();

        ResultSet columns = meta.getColumns(catalog, schema, tableName, "%");
        while(columns.next()){
            String columnName = columns.getString("COLUMN_NAME");
            int sqlType = columns.getInt("DATA_TYPE");

            if(primaryKeys.contains(columnName)){
                sb.append("    @PrimaryKey");
                if(generationType == null){
                    generationType = GenerationType.APPLICATION;
                }
                sb.append("(generationType=GenerationType.").append(generationType.name());
                if(generationType == GenerationType.SEQUENCE){
                    sb.append(", generator=\"").append(tableName).append("_").append(columnName).append("_SEQ\"");
                }
                sb.append(")");
                sb.append(LINE_SEPARATOR);
            }

            sb.append("    @Column(name=\"").append(columnName).append("\")").append(LINE_SEPARATOR);

            sb.append("    public ").append(getJavaTypeName(sqlType)).append(" ").
                append(nameConverter.columnToProperty(columnName)).append(";").append(LINE_SEPARATOR);

            sb.append(LINE_SEPARATOR);
        }
        columns.close();

        sb.append("}").append(LINE_SEPARATOR);
        return sb.toString();
    }

    /**
     * Returns the Groovy source code of the entity class which corresponds to the specified table.
     *
     * @param conn the DB connection
     * @param tableName the DB table name
     * @param catalog the DB catalog
     * @param schema the DB schema
     *
     * @return the source
     *
     * @throws SQLException if a something goes wrong when working with the database
     */
    protected String getGroovyEntitySource(Connection conn,
                                       String tableName, String catalog, String schema) throws SQLException {

        StringBuilder sb = new StringBuilder();

        // package declaration
        sb.append("package ").append(packageName).append(LINE_SEPARATOR);
        sb.append(LINE_SEPARATOR);

        // import statements
        appendImportGroovy(sb, Table.class);
        appendImportGroovy(sb, Column.class);
        appendImportGroovy(sb, PrimaryKey.class);
        appendImportGroovy(sb, GenerationType.class);
        sb.append(LINE_SEPARATOR);

        // class declaration
        sb.append("@Table(name=\"").append(tableName).append("\")").append(LINE_SEPARATOR);
        sb.append("class ").append(tableToEntity(tableName)).append(" {").append(LINE_SEPARATOR);
        sb.append(LINE_SEPARATOR);

        // Entity properties
        DatabaseMetaData meta = conn.getMetaData();
        List<String> primaryKeys = new ArrayList<>();

        ResultSet keys = meta.getPrimaryKeys(catalog, schema, tableName);
        while(keys.next()){
            String columnName = keys.getString("COLUMN_NAME");
            primaryKeys.add(columnName);
        }
        keys.close();

        ResultSet columns = meta.getColumns(catalog, schema, tableName, "%");
        while(columns.next()){
            String columnName = columns.getString("COLUMN_NAME");
            int sqlType = columns.getInt("DATA_TYPE");

            if(primaryKeys.contains(columnName)){
                sb.append("    @PrimaryKey");
                if(generationType == null){
                    generationType = GenerationType.APPLICATION;
                }
                sb.append("(generationType=GenerationType.").append(generationType.name());
                if(generationType == GenerationType.SEQUENCE){
                    sb.append(", generator=\"").append(tableName).append("_").append(columnName).append("_SEQ\"");
                }
                sb.append(")");
                sb.append(LINE_SEPARATOR);
            }

            sb.append("    @Column(name=\"").append(columnName).append("\")").append(LINE_SEPARATOR);

            sb.append("    ").append(getJavaTypeName(sqlType)).append(" ").
                    append(nameConverter.columnToProperty(columnName)).append(LINE_SEPARATOR);

            sb.append(LINE_SEPARATOR);
        }
        columns.close();

        sb.append("}").append(LINE_SEPARATOR);
        return sb.toString();
    }

    /**
     * Returns the XML source code of the entity class which corresponds to the specified table.
     *
     * @param conn the DB connection
     * @param tableName the DB table name
     * @param catalog the DB catalog
     * @param schema the DB schema
     *
     * @return the source
     *
     * @throws SQLException if a something goes wrong when working with the database
     */
    protected String getXmlEntitySource(Connection conn,
                                           String tableName, String catalog, String schema) throws SQLException {

        StringBuilder sb = new StringBuilder();

        // header declaration
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(LINE_SEPARATOR);

        // class declaration
        sb.append("<entity name=\"").append(tableToEntity(tableName)).append("\" ");
        sb.append("table=\"").append(tableName).append("\">").append(LINE_SEPARATOR);

        // Entity properties
        DatabaseMetaData meta = conn.getMetaData();
        List<String> primaryKeys = new ArrayList<>();

        ResultSet keys = meta.getPrimaryKeys(catalog, schema, tableName);
        while(keys.next()){
            String columnName = keys.getString("COLUMN_NAME");
            primaryKeys.add(columnName);
        }
        keys.close();

        ResultSet columns = meta.getColumns(catalog, schema, tableName, "%");
        while(columns.next()){
            String columnName = columns.getString("COLUMN_NAME");
            int sqlType = columns.getInt("DATA_TYPE");

            StringBuilder sbPk = new StringBuilder();
            if(primaryKeys.contains(columnName)){
                sbPk.append(" pk=\"true\"");
                if(generationType == null){
                    generationType = GenerationType.APPLICATION;
                }
                sbPk.append(" generation=\"").append(generationType.name()).append("\"");
                if(generationType == GenerationType.SEQUENCE){
                    sbPk.append(" generator=\"").append(tableName).append("_").append(columnName).append("_SEQ\"");
                }
            }

            sb.append("  <attribute name=\"").append(nameConverter.columnToProperty(columnName)).append("\" type=\"")
                    .append(getJavaTypeName(sqlType).toLowerCase()).append("\">").append(LINE_SEPARATOR);
            sb.append("    <column name=\"").append(columnName).append("\"").append(sbPk).append("/>").append(LINE_SEPARATOR);

            sb.append("  </attribute>").append(LINE_SEPARATOR);
        }
        columns.close();

        sb.append("</entity>").append(LINE_SEPARATOR);
        return sb.toString();
    }

    /**
     * Validates the 'lang' parameter.
     *
     * @param lang the language to generate to. Supported are 'java', 'groovy' and 'xml'
     *
     * @return true if the parameter is valid
     */
    protected static boolean validateLang(String lang) {
        if(lang == null || !("java".equals(lang) || "groovy".equals(lang) || "xml".equals(lang))) {
            throw new IllegalArgumentException("Argument 'lang' must be 'java', 'groovy' or 'xml' only!");
        }
        return true;
    }

    /**
     * Generates the entity source file into the given source directory.
     *
     * @param srcDir directory where to generate the Java sources.
     * @param charset character set
     * @param conn the DB connection
     * @param tableName the DB table name
     * @param catalog the DB catalog
     * @param schema the DB schema
     * @param lang the language to generate to. Supported are 'java' and 'groovy'
     *
     * @throws SQLException if a something goes wrong when working with the database
     * @throws IOException if the java file can't be written
     */
    public void saveEntitySource(File srcDir, String charset, Connection conn,
            String tableName, String catalog, String schema, String lang) throws SQLException, IOException {

        String source = getEntitySource(conn, tableName, catalog, schema);
        String packageDir = packageName.replace('.', '/');
        String fileName = packageDir + "/" + tableToEntity(tableName) + "."+lang;

        File file = new File(srcDir, fileName);
        createDir(file.getParentFile());

        FileOutputStream out = new FileOutputStream(file);
        out.write(source.getBytes(charset));
        out.close();
    }

    /**
     * Generates the entity source files for all tables in the specified catalog and schema, that respect the specified
     * pattern, except those ignored.
     *
     * @param srcDir the directory where the generated sources will be written
     * @param charset the used Character set
     * @param conn the database connection
     * @param catalogS the catalogue
     * @param schemaS the String
     * @param tableNamePattern positive pattern for the tables to generate entities for
     * @param ignoreTableNamePattern negative pattern for the table NOT to generate entities for
     * @param lang the language to generate to. Supported are 'java' and 'groovy'
     *
     * @throws SQLException if a something goes wrong when working with the database
     * @throws IOException if the Java files can't be written
     */
    public void saveAllEntitySources(File srcDir, String charset, Connection conn,String catalogS, String schemaS,
                                     String tableNamePattern, String ignoreTableNamePattern, String lang) throws SQLException, IOException {

        DatabaseMetaData meta = conn.getMetaData();
        ResultSet rs = meta.getTables(catalogS, schemaS, "%", new String[]{"TABLE"});
        while(rs.next()){
            String catalog = rs.getString("TABLE_CAT");
            String schema = rs.getString("TABLE_SCHEM");
            String tableName = rs.getString("TABLE_NAME");

            if(StringUtil.isNotEmpty(tableNamePattern)){
                if(!tableName.matches(tableNamePattern)){
                    continue;
                }
            }

            if(StringUtil.isNotEmpty(ignoreTableNamePattern)
                    && tableName.matches(ignoreTableNamePattern)){
                continue;
            }

            this.saveEntitySource(srcDir, charset, conn, tableName, catalog, schema, lang);

            System.out.println(String.format("  %s.%s", schema, tableName));
        }
        JdbcUtil.close(rs);
    }

    private static void createDir(File dir){
        if(!dir.getParentFile().exists()){
            createDir(dir.getParentFile());
        }
        if(!dir.exists()){
            dir.mkdir();
        }
    }

    private String getJavaTypeName(int sqlType){
        for(ValueType<?> valueType: getValueTypes()){
            Class<?> type = valueType.getJavaType(sqlType);
            if(type != null){
                String typeName = type.getName();
                typeName = typeName.replaceFirst("^java\\.lang\\.", "");
                return typeName;
            }
        }
        return "String";
    }

    private List<ValueType<?>> getValueTypes(){
        List<ValueType<?>> valueTypes = new ArrayList<>();

        if(this.dialect.getValueType() != null){
            valueTypes.add(this.dialect.getValueType());
        }

        valueTypes.addAll(this.valueTypes);

        return valueTypes;
    }

    private static void appendImport(StringBuilder sb, Class<?> clazz){
        sb.append("import ").append(clazz.getName().replace('$', '.')).append(";").append(LINE_SEPARATOR);
    }

    private static void appendImportGroovy(StringBuilder sb, Class<?> clazz){
        sb.append("import ").append(clazz.getName().replace('$', '.')).append(LINE_SEPARATOR);
    }

    private static String tableToEntity(String tableName){
        StringBuilder sb = new StringBuilder();
        boolean uppercase = true;
        for(int i=0; i < tableName.length(); i++){
            String letter = tableName.substring(i, i + 1);
            if(letter.equals("_")){
                uppercase = true;
                continue;
            }
            if(uppercase){
                sb.append(letter.toUpperCase());
            } else {
                sb.append(letter.toLowerCase());
            }
            uppercase = false;
        }
        return sb.toString();
    }

}
