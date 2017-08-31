package com.miragesql.miragesql;


/**
 * {@link EntityCreationFailedException} is thrown to indicate that
 * an {@link EntityOperator} failed to create a result entity.
 * 
 * @see EntityOperator
 * @author daisuke
 */
@SuppressWarnings("serial")
public class EntityCreationFailedException extends RuntimeException {

    public EntityCreationFailedException() {
        super();
    }

    public EntityCreationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityCreationFailedException(String message) {
        super(message);
    }

    public EntityCreationFailedException(Throwable cause) {
        super(cause);
    }
}
