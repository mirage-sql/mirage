package com.miragesql.miragesql;

import java.util.Objects;

import com.miragesql.miragesql.annotation.Column;
import com.miragesql.miragesql.annotation.PrimaryKey;
import com.miragesql.miragesql.annotation.Transient;

public class SqlManagerImplTest_Immutable extends AbstractDatabaseTest {

    public SqlManagerImplTest_Immutable() {
        super();
        sqlManager.setEntityOperator(new ImmutableEntityOperator());
    }
    
    public void testGetSingleResultByMap() throws Exception {
        Subscription subscription = new Subscription(1L);
        sqlManager.insertEntity(subscription);
    
        Subscription actual = sqlManager.findEntity(Subscription.class, 1L);
        assertEquals(subscription, actual);
    }
    
    public static class Subscription {

        @PrimaryKey(generationType= PrimaryKey.GenerationType.APPLICATION)
        public final Long userId;
        
        @Transient
        public final String computedProperty;
        
        public Subscription(@Column(name = "USER_ID") Long userId) {
            this.userId = userId;
            this.computedProperty = userId.toString();
        }
    
        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Subscription that = (Subscription) o;
            return Objects.equals(userId, that.userId);
        }
    
        @Override
        public int hashCode() {
            return Objects.hash(userId);
        }
    }

}
