package framework.runtime;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

import framework.schema.SchemaDefinition;


/**
 * Responsible for producing schema-aware textual representations of objects.
 * <p>
 * {@code ObjectViewer} ensures that:
 * <ul>
 *   <li>Only fields exposed by a schema are displayed</li>
 *   <li>Object references are shown via their registry names</li>
 *   <li>Collections are expanded into readable lists</li>
 * </ul>
 * This class performs no validation — it relies on upstream invariants.
 */

public class ObjectViewer {
    ObjectRepository repository; 

    
    /**
     * Constructs a viewer bound to the given repository.
     *
     * @param repository object repository
     */

    public ObjectViewer(ObjectRepository repository){
        this.repository = repository; 
    }

    
    /**
     * Produces a string representation of an object under a specific schema.
     *
     * @param schemaObject object instance
     * @param schema schema definition controlling visibility
     * @return formatted object display
     * @throws ObjectException if field access or display fails
     */

    public String displaySchemaObject(Object schemaObject, SchemaDefinition schema) throws ObjectException{
        String output = "";
        Class<?> objectClass = schemaObject.getClass(); 
        output += repository.getObjectName(schemaObject) + "\n";
        Field[] fields = objectClass.getDeclaredFields(); 
        if (!(schema.getFieldList().size() == 0)){
            output += "Object Fields:\n"; 
        }
        for (Field feld : fields){
            String displayValue = "";
            try {
                boolean match = false; 
                for (String userField : schema.getFieldList()){
                    if (userField.equals(feld.getName())){
                        match = true; 
                    } 
                }
                if (!match){
                    continue; 
                }
                feld.setAccessible(true);
                Object fieldValue = feld.get(schemaObject); 
                if (feld.getGenericType() instanceof ParameterizedType){
                    ParameterizedType pType = (ParameterizedType) feld.getGenericType();
                    Class<?> rawType = (Class<?>) pType.getRawType();
                    if (rawType.equals(ArrayList.class)){
                        for (Object element : (ArrayList) fieldValue){
                            displayValue += repository.getObjectName(element) + ", ";
                        }
                        displayValue = displayValue.replaceAll(", +$", ""); 
                    } else {
                        throw new ObjectException("Unsupported collection type found while displaying object");
                    }
                } else if (repository.getInstanceToObjectNameMap().containsKey(fieldValue)) {
                    displayValue = repository.getObjectName(fieldValue); 
                } else {
                    displayValue = fieldValue.toString(); 
                }
                output += feld.getName() + " : " + displayValue + "\n";
            } catch (IllegalAccessException e) {
                throw new ObjectException("Could not access field value while trying to display object");
            }
        }
        return output; 
    }
    
}
