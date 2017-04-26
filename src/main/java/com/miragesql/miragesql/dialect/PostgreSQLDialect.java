package jp.sf.amateras.mirage.dialect;

import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import jp.sf.amateras.mirage.type.PostgreResultSetValueType;
import jp.sf.amateras.mirage.type.ValueType;

// TODO LargeObject support
public class PostgreSQLDialect extends StandardDialect {

	private PostgreResultSetValueType valueType = new PostgreResultSetValueType();

	@Override /**{@inheritDoc}**/
    public String getName() {
        return "postgresql";
    }

	@Override /**{@inheritDoc}**/
    public ValueType<?> getValueType(){
    	return valueType;
    }

	@Override /**{@inheritDoc}**/
	public String getSequenceSql(String sequenceName) {
		return String.format("SELECT NEXTVAL('%s')", sequenceName);
	}

	@Override /**{@inheritDoc}**/
	public boolean supportsGenerationType(GenerationType generationType) {
		if(generationType == GenerationType.IDENTITY){
			return false;
		}
		return true;
	}


}
