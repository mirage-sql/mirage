package com.miragesql.miragesql.test;

import com.miragesql.miragesql.SqlManager;
import com.miragesql.miragesql.SqlManagerImpl;

/**
 * The mock class of {@link SqlManager} for unit testing.
 * <p>
 * You can run your code which uses <code>SqlManager</code> without database
 * using this class instead of {@link SqlManagerImpl}.
 * You can also verify executed SQLs via {@link MirageTestContext}.
 *
 * @author Naoki Takezoe
 * @see MockSqlExecuter
 * @see MockCallExecuter
 * @see MirageTestContext
 */
public class MockSqlManager extends SqlManagerImpl {

    /**
     * Constructor.
     */
    public MockSqlManager() {
        super();

        this.sqlExecutor = new MockSqlExecuter();
        this.sqlExecutor.setNameConverter(this.nameConverter);
        this.sqlExecutor.setDialect(this.dialect);

        this.callExecutor = new MockCallExecuter();
        this.callExecutor.setNameConverter(this.nameConverter);
        this.callExecutor.setDialect(this.dialect);
    }

    // TODO GenerationType.SEQUENCE processing

}
