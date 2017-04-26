package jp.sf.amateras.mirage.type;


public class OracleResultSetValueType extends AbstractResultSetValueType {

	public static final int CURSOR = -10;

	public OracleResultSetValueType() {
		super(CURSOR);
	}

}
