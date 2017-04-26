package jp.sf.amateras.mirage.dialect;

public class SQLiteDialect extends StandardDialect {

    @Override /**{@inheritDoc}**/
    public String getName() {
        return "sqlite";
    }

}
