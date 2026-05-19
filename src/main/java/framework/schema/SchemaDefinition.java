package framework.schema;
import java.util.ArrayList;


/**
 * Represents a schema definition in the framework.
 * <p>
 * A {@code SchemaDefinition} binds together:
 * <ul>
 *   <li>A schema name (runtime identifier)</li>
 *   <li>A base class name that the schema projects over</li>
 *   <li>A list of fields exposed by the schema</li>
 * </ul>
 * Schemas act as runtime views over a base class, controlling which
 * fields may be set, displayed, and mutated.
 */

public class SchemaDefinition {
    String schema = "";
    String base = "";
    ArrayList<String> fieldList = new ArrayList<>(); 
    
    /**
     * Constructs a new schema definition.
     *
     * @param schema name of the schema
     * @param base simple name of the base class
     * @param fieldList list of fields included in the schema
     */

    public SchemaDefinition(String schema, String base, ArrayList<String> fieldList){
        this.schema = schema;
        this.base = base; 
        this.fieldList = fieldList; 
        // Initialize method
    }
    
    
    /**
     * Returns the schema name.
     *
     * @return schema name
     */

    public String getSchemaName() {
        return schema;
    }

    
    /**
     * Returns the simple name of the base class.
     *
     * @return base class name
     */

    public String getBase() {
        return base;
    }

    
    /**
     * Returns the list of fields exposed by the schema.
     *
     * @return list of field names
     */

    public ArrayList<String> getFieldList() {
        return fieldList;
    }
}
