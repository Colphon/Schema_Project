package framework.schema;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set; 


/**
 * Registry responsible for managing schemas and schema-eligible base classes.
 * <p>
 * The {@code SchemaRegistry}:
 * <ul>
 *   <li>Tracks discovered schema-eligible classes</li>
 *   <li>Enforces schema naming constraints</li>
 *   <li>Performs base class lookups and validation</li>
 *   <li>Stores defined schemas</li>
 * </ul>
 * This class serves as the authoritative source for schema metadata
 * throughout the framework.
 */

public class SchemaRegistry {
    Set<Class<?>> classes; 
    ArrayList<SchemaDefinition> schemas = new ArrayList<>();

    
    /**
     * Constructs a new registry with the given set of base classes.
     *
     * @param classes schema-eligible base classes
     */

    public SchemaRegistry(Set<Class<?>> classes){
        this.classes = classes; 
    }

    
    /**
     * Returns all discovered schema-eligible base classes.
     *
     * @return set of base classes
     */

    public Set<Class<?>> getClasses() {
        return classes;
    }

    
    /**
     * Locates and validates a base class by its simple name.
     *
     * @param base simple name of the base class
     * @return matching base class
     * @throws SchemaException if the class does not exist,
     *                         is an interface, or is abstract
     */

    public Class<?> findBaseClass(String base)throws SchemaException{
        boolean found = false; 
        Class<?> baseClass = null; 
        for (Class<?> clazz : classes){
            if (clazz.getSimpleName().equals(base)){
                found = true;
                baseClass = clazz; 
            }
        }
        if (!found){
            throw new SchemaException("Base class not found"); 
        }

        if (baseClass.isInterface()){
            throw new SchemaException("Base class is an interface");
        }
        
        if (Modifier.isAbstract(baseClass.getModifiers())){
            throw new SchemaException("Base class is abstract"); 
        }
        return baseClass; 
    }

    
    /**
     * Finds a schema definition by name.
     *
     * @param schemaPick schema name
     * @return schema definition
     * @throws SchemaException if the schema does not exist
     */

    public SchemaDefinition findSchema(String schemaPick)throws SchemaException{
        SchemaDefinition foundSchema = null; 
        for (SchemaDefinition schemaDefinition : schemas){
            if (schemaDefinition.getSchemaName().equals(schemaPick)){
                foundSchema = schemaDefinition; 
            }
        }
        if (foundSchema == null){
            throw new SchemaException("Schema not found"); 
        }
        return foundSchema; 
    }

    
    /**
     * Enforces schema naming constraints:
     * <ul>
     *   <li>Schema name must not be empty</li>
     *   <li>Schema name must not collide with base class names</li>
     *   <li>Schema name must be unique</li>
     * </ul>
     *
     * @param schema proposed schema name
     * @throws SchemaException if naming rules are violated
     */

    public void noDuplicates(String schema) throws SchemaException{
        if (schema.isEmpty()){
            throw new SchemaException("Schema must have a name"); 
        }
        for (Class<?> clazz : classes){
            if (clazz.getSimpleName().equals(schema)){
                throw new SchemaException("Name is identical to base class " + clazz.getSimpleName()); 
            }
        }
        for (SchemaDefinition schemaDefinition : schemas){
            if (schema.equals(schemaDefinition.getSchemaName())){
                throw new SchemaException("A Schema with that name already exists"); 
            }
        }
    }

    
    /**
     * Registers a new schema definition.
     *
     * @param definition schema definition to add
     */

    public void addSchema(SchemaDefinition definition){
        schemas.add(definition); 
    }

    
    /**
     * Returns all registered schemas.
     *
     * @return list of schema definitions
     */

    public ArrayList<SchemaDefinition> getSchemas() {
        return schemas;
    }
}
