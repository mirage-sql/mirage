package jp.sf.amateras.mirage.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.sf.amateras.mirage.annotation.Column;
import jp.sf.amateras.mirage.annotation.PrimaryKey;
import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import jp.sf.amateras.mirage.annotation.Table;
import jp.sf.amateras.mirage.dialect.Dialect;
import jp.sf.amateras.mirage.naming.NameConverter;
import jp.sf.amateras.mirage.type.BigDecimalValueType;
import jp.sf.amateras.mirage.type.BooleanPrimitiveValueType;
import jp.sf.amateras.mirage.type.BooleanValueType;
import jp.sf.amateras.mirage.type.ByteArrayValueType;
import jp.sf.amateras.mirage.type.DoublePrimitiveValueType;
import jp.sf.amateras.mirage.type.DoubleValueType;
import jp.sf.amateras.mirage.type.FloatPrimitiveValueType;
import jp.sf.amateras.mirage.type.FloatValueType;
import jp.sf.amateras.mirage.type.IntegerPrimitiveValueType;
import jp.sf.amateras.mirage.type.IntegerValueType;
import jp.sf.amateras.mirage.type.LongPrimitiveValueType;
import jp.sf.amateras.mirage.type.LongValueType;
import jp.sf.amateras.mirage.type.ShortPrimitiveValueType;
import jp.sf.amateras.mirage.type.ShortValueType;
import jp.sf.amateras.mirage.type.SqlDateValueType;
import jp.sf.amateras.mirage.type.StringValueType;
import jp.sf.amateras.mirage.type.TimeValueType;
import jp.sf.amateras.mirage.type.TimestampValueType;
import jp.sf.amateras.mirage.type.UtilDateValueType;
import jp.sf.amateras.mirage.type.ValueType;

// TODO PrimaryKeyのgenerationTypeを指定できるようにする

/**
 * Entity Generation Tool.
 * <pre> // setup
 * EntityGen gen = new EntityGen();
 * gen.setPackageName("jp.sf.amateras.mirage.entity");
 * gen.setNameConverter(new DefaultNameConverter());
 * gen.setDialect(new StandardDialect());
 *
 * // generate
 * String source = gen.getEntitySource(conn, "BOOK", null, null); </pre>
 * <ul>
 *   <li>TODO - Generates Javadoc</li>
 * </ul>
 * @author Naoki Takezoe
 */
public class EntityGen {

	private String packageName;
	private NameConverter nameConverter;
	private List<ValueType<?>> valueTypes = new ArrayList<ValueType<?>>();
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
//		addValueType(new jp.sf.amateras.mirage.type.DefaultValueType());
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
	 * @throws SQLException 
	 */
	public String getEntitySource(Connection conn,
			String tableName, String catalog, String schema) throws SQLException {

		StringBuilder sb = new StringBuilder();

		// package decl
		sb.append("package ").append(packageName).append(";").append(LINE_SEPARATOR);
		sb.append(LINE_SEPARATOR);

		// import statements
		appendImport(sb, Table.class);
		appendImport(sb, Column.class);
		appendImport(sb, PrimaryKey.class);
		appendImport(sb, GenerationType.class);
		sb.append(LINE_SEPARATOR);

		// class decl
		sb.append("@Table(name=\"").append(tableName).append("\")").append(LINE_SEPARATOR);
		sb.append("public class ").append(tableToEntity(tableName)).append(" {").append(LINE_SEPARATOR);
		sb.append(LINE_SEPARATOR);

		// properties
		DatabaseMetaData meta = conn.getMetaData();
		List<String> primaryKeys = new ArrayList<String>();

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
	 * Generates the entity source file into the given source directory.
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public void saveEntitySource(File srcDir, String charset, Connection conn,
			String tableName, String catalog, String schema) throws SQLException, IOException {

		String source = getEntitySource(conn, tableName, catalog, schema);
		String packageDir = packageName.replace('.', '/');
		String fileName = packageDir + "/" + tableToEntity(tableName) + ".java";

		File file = new File(srcDir, fileName);
		createDir(file.getParentFile());

		FileOutputStream out = new FileOutputStream(file);
		out.write(source.getBytes(charset));
		out.close();
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
		List<ValueType<?>> valueTypes = new ArrayList<ValueType<?>>();

		if(this.dialect.getValueType() != null){
			valueTypes.add(this.dialect.getValueType());
		}

		for(ValueType<?> valueType: this.valueTypes){
			valueTypes.add(valueType);
		}

		return valueTypes;
	}

	private static void appendImport(StringBuilder sb, Class<?> clazz){
		sb.append("import ").append(clazz.getName().replace('$', '.')).append(";").append(LINE_SEPARATOR);
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
