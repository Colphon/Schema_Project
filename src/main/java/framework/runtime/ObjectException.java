package framework.runtime;

/**
 * Custom checked exception used to signal
 * business-rule and validation errors
 * within the schema system.
 */

public class ObjectException extends Exception {
    
    /**
     * Constructs a ObjectException with no message.
     */

    public ObjectException() {}
    
    /**
     * Constructs a ObjectException with a specific error message.
     *
     * @param message description of the object error
     */

    public ObjectException(String message) {
        super(message);
    }
}

