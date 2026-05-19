package framework.schema;

/**
 * Custom checked exception used to signal
 * business-rule and validation errors
 * within the schema system.
 */

public class SchemaException extends Exception {
    
    /**
     * Constructs a SchemaException with no message.
     */

    public SchemaException() {}
    
    /**
     * Constructs a SchemaException with a specific error message.
     *
     * @param message description of the schema error
     */

    public SchemaException(String message) {
        super(message);
    }
}

