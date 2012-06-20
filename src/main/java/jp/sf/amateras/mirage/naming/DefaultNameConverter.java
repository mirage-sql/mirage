package jp.sf.amateras.mirage.naming;

/**
 * The default implementation of {@link NameConverter}.
 * <p>
 * This implementation uses camelcase for entity / property names, and underscore for table / column names.
 *
 * @author Naoki Takezoe
 */
public class DefaultNameConverter implements NameConverter {

//	@Override
	public String columnToProperty(String columnName) {
		StringBuilder sb = new StringBuilder();
		boolean uppercase = false;

		for(int i=0;i<columnName.length();i++){
			char c = columnName.charAt(i);
			if(c == '_'){
				uppercase = true;
			} else {
				if(uppercase){
					sb.append(String.valueOf(c).toUpperCase());
					uppercase = false;
				} else {
					sb.append(String.valueOf(c).toLowerCase());
				}
			}
		}

		return sb.toString();
	}

//	@Override
	public String entityToTable(String entityName) {
		int index = entityName.lastIndexOf('.');
		if(index >= 0){
			entityName = entityName.substring(index + 1);
		}
		index = entityName.lastIndexOf('$');
		if(index >= 0){
			entityName = entityName.substring(index + 1);
		}

		StringBuilder sb = new StringBuilder();

		for(int i=0;i<entityName.length();i++){
			char c = entityName.charAt(i);
			if(c >= 'A' && c <= 'Z' && sb.length() > 0){
				sb.append('_');
			}
			sb.append(String.valueOf(c).toUpperCase());
		}

		return sb.toString();
	}

//	@Override
	public String propertyToColumn(String propertyName) {
		StringBuilder sb = new StringBuilder();

		for(int i=0;i<propertyName.length();i++){
			char c = propertyName.charAt(i);
			if(c >= 'A' && c <= 'Z'){
				sb.append('_');
			}
			sb.append(String.valueOf(c).toUpperCase());
		}

		return sb.toString();

	}

//	@Override
//	public String tableToEntity(String tableName) {
//		return null;
//	}

}
