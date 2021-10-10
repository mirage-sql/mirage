package com.miragesql.miragesql.tools;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import com.miragesql.miragesql.bean.ReflectiveOperationFailedException;
import com.miragesql.miragesql.dialect.Dialect;
import com.miragesql.miragesql.dialect.StandardDialect;
import com.miragesql.miragesql.naming.DefaultNameConverter;
import com.miragesql.miragesql.naming.NameConverter;
import com.miragesql.miragesql.type.ValueType;
import com.miragesql.miragesql.util.JdbcUtil;
import com.miragesql.miragesql.util.StringUtil;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant task to generate entity source files from a database schema.
 * <p>
 * This is an example of build.xml:
 * <pre>&lt;project name="Mirage-SQL EntityGen" basedir="." default="gen_entity"&gt;
 *
 *  &lt;target name="gen_entity"&gt;
 *
 *    &lt;path id="class.path"&gt;
 *      &lt;fileset dir="lib"&gt;
 *        &lt;include name="*.jar" /&gt;
 *      &lt;/fileset&gt;
 *    &lt;/path&gt;
 *
 *    &lt;taskdef name="entityGen"
 *      classname="com.miragesql.miragesql.tool.EntityGenTask"
 *      classpathref="class.path" /&gt;
 *
 *    &lt;entityGen
 *        driver="org.hsqldb.jdbc.JDBCDriver"
 *        url="jdbc:hsqldb:mem:mirage_test"
 *        user="sa"
 *        password="" /&gt;
 *
 *  &lt;/target&gt;
 *
 * &lt;/project&gt;</pre>
 *
 * <table border="1">
 *     <caption>EntityGenTask has following attributes:</caption>
 *   <tr>
 *     <th>name</th>
 *     <th>required</th>
 *     <th>description</th>
 *     <th>default</th>
 *   </tr>
 *   <tr>
 *     <td>driver</td>
 *     <td>yes</td>
 *     <td>JDBC driver class name</td>
 *     <td>&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>url</td>
 *     <td>yes</td>
 *     <td>JDBC connection url</td>
 *     <td>&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>user</td>
 *     <td>yes</td>
 *     <td>JDBC connection user</td>
 *     <td>&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>password</td>
 *     <td>yes</td>
 *     <td>JDBC connection password</td>
 *     <td>&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>packageName</td>
 *     <td>&nbsp;</td>
 *     <td>Package name of generated entity classes</td>
 *     <td>entity</td>
 *   </tr>
 *   <tr>
 *     <td>outputDir</td>
 *     <td>&nbsp;</td>
 *     <td>Output directory of generated entity classes</td>
 *     <td>.</td>
 *   </tr>
 *   <tr>
 *     <td>charset</td>
 *     <td>&nbsp;</td>
 *     <td>Charset of generated entity source files</td>
 *     <td>UTF-8</td>
 *   </tr>
 *   <tr>
 *     <td>nameConverter</td>
 *     <td>&nbsp;</td>
 *     <td>Class name of {@link NameConverter}</td>
 *     <td>com.miragesql.miragesql.naming.DefaultNameConverter</td>
 *   </tr>
 *   <tr>
 *     <td>dialect</td>
 *     <td>&nbsp;</td>
 *     <td>Class name of {@link Dialect}</td>
 *     <td>com.miragesql.miragesql.dialect.StandardDialect</td>
 *   </tr>
 *   <tr>
 *     <td>valueTypes</td>
 *     <td>&nbsp;</td>
 *     <td>Class names of {@link ValueType} as comma separated value</td>
 *     <td>&nbsp;</td>
 *   </tr>
 *   <!--
 *   <tr>
 *     <td>persistancePrimaryKey</td>
 *     <td>&nbsp;</td>
 *     <td>Whether persist primary key property, or not</td>
 *     <td>true</td>
 *   </tr>
 *   -->
 *   <tr>
 *     <td>catalog</td>
 *     <td>&nbsp;</td>
 *     <td>Catalog name</td>
 *     <td>null</td>
 *   </tr>
 *   <tr>
 *     <td>schema</td>
 *     <td>&nbsp;</td>
 *     <td>Schema name</td>
 *     <td>null</td>
 *   </tr>
 *   <tr>
 *     <td>tableNamePattern</td>
 *     <td>&nbsp;</td>
 *     <td>Target table name as regular expression</td>
 *     <td>&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>ignoreTableNamePattern</td>
 *     <td>&nbsp;</td>
 *     <td>Ignore table name as regular expression</td>
 *     <td>&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>lang</td>
 *     <td>&nbsp;</td>
 *     <td>The programming language this code is generated for.</td>
 *     <td>java</td>
 *   </tr>
 * </table>
 *
 *
 * @author Naoki Takezoe
 */
public class EntityGenTask extends Task {

    private String driver = "";
    private String url = "";
    private String user = "";
    private String password = "";
    private String packageName = "entity";
    private String outputDir = ".";
    private String charset = "UTF-8";
    private String nameConverter = DefaultNameConverter.class.getName();
    private String dialect = StandardDialect.class.getName();
    private String valueTypes = "";
//	private boolean persistancePrimaryKey = true;
    private String generationType = "";
    private String catalog = null;
    private String schema = null;
    private String tableNamePattern = "";
    private String ignoreTableNamePattern = "";
    private String lang = "java";

    /**
     * Sets the Dialect class name.
     * The default value is "com.miragesql.miragesql.dialect.StandardDialect".
     *
     * @param dialect the dialect class name
     */
    public void setDialect(String dialect){
        this.dialect = dialect;
    }

    /**
     * Sets the package name.
     *
     * @param packageName the package name
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Sets the NameConverter class name.
     * The default value is "com.miragesql.miragesql.naming.DefaultNameConverter".
     *
     * @param nameConverter the name converter
     */
    public void setNameConverter(String nameConverter) {
        this.nameConverter = nameConverter;
    }

    /**
     * Sets the generation type of primary keys.
     *
     * @param generationType APPLICATION, IDENTITY or SEQUENCE
     */
    public void setGenerationType(String generationType){
        this.generationType = generationType;
    }

    /**
     * Sets the ValueType class names.
     * You can specify two oe more class names as comma separated value.
     *
     * @param valueType the ValueType class names
     */
    public void setValueType(String valueType){
        this.valueTypes = valueType;
    }

    /**
     * Sets the JDBC drive class name.
     *
     * @param driver the JDBC drive class name
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * Sets the JDBC connection URL.
     *
     * @param url the JDBC connection URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Sets the JDBC user.
     *
     * @param user the JDBC user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Sets the JDBC password.
     *
     * @param password the JDBC password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the output directory.
     *
     * @param outputDir the output directory
     */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Sets the charset of the generated entity source file.
     *
     * @param charset the charset of the generated source file
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * Sets the catalog name
     *
     * @param catalog the catalog name
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    /**
     * Sets the schema name
     *
     * @param schema the schema name
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * Sets the target table name pattern as regular expression.
     *
     * @param tableNamePattern the target table name pattern
     */
    public void setTableNamePattern(String tableNamePattern) {
        this.tableNamePattern = tableNamePattern;
    }

    /**
     * Sets the ignore table name pattern as regular expression.
     *
     * @param ignoreTableNamePattern the ignore table name pattern
     */
    public void setIgnoreTableNamePattern(String ignoreTableNamePattern) {
        this.ignoreTableNamePattern = ignoreTableNamePattern;
    }

    /**
     * Sets the language to generate to. The default value is 'java'.
     *
     * @param lang the language to generate to. Supported are 'java' and 'groovy'
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public void execute() throws BuildException {
        // validate parameters
        if(StringUtil.isEmpty(driver)){
            throw new BuildException("driver is required.");
        }
        if(StringUtil.isEmpty(url)){
            throw new BuildException("url is required.");
        }

        try {
            EntityGen entityGen = new EntityGen();

            entityGen.setPackageName(packageName);
            entityGen.setDialect(newInstance(Dialect.class, this.dialect));
            entityGen.setNameConverter(newInstance(NameConverter.class, this.nameConverter));
            if(StringUtil.isNotEmpty(generationType)){
                if(generationType.equals("APPLICATION")){
                    entityGen.setGenerationType(GenerationType.APPLICATION);
                } else if(generationType.equals("IDENTITY")){
                    entityGen.setGenerationType(GenerationType.IDENTITY);
                } else if(generationType.equals("SEQUENCE")){
                    entityGen.setGenerationType(GenerationType.SEQUENCE);
                }
            }

            if(StringUtil.isNotEmpty(this.valueTypes)){
                for(String valueType: valueTypes.split(",")){
                    entityGen.addValueType(newInstance(ValueType.class, valueType.trim()));
                }
            }

            Connection conn = createConnection();

            try {
                System.out.println("Generating entities for language "+lang+"...");

                entityGen.saveAllEntitySources(new File(outputDir), charset, conn, this.catalog, this.schema,
                        tableNamePattern, ignoreTableNamePattern, lang);
            } finally {
                JdbcUtil.close(conn);
            }

        } catch(Exception ex){
            throw new BuildException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T newInstance(Class<T> type, String className) throws ReflectiveOperationFailedException {
        try {
            return (T) Class.forName(className).newInstance();

        } catch (InstantiationException e) {
            throw new ReflectiveOperationFailedException(e);

        } catch (IllegalAccessException e) {
            throw new ReflectiveOperationFailedException(e);

        } catch (ClassNotFoundException e) {
            throw new ReflectiveOperationFailedException(e);

        }
    }

    private Connection createConnection() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, user, password);
        return conn;
    }

}
