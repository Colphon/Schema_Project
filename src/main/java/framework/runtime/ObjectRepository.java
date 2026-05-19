package framework.runtime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map; 
import java.util.Set; 


/**
 * Central repository for managing runtime object instances.
 * <p>
 * The {@code ObjectRepository} is responsible for:
 * <ul>
 *   <li>Mapping object names to runtime instances</li>
 *   <li>Maintaining reverse mappings from instances to names</li>
 *   <li>Tracking which objects belong to which schemas</li>
 * </ul>
 * It serves as the single source of truth for object identity
 * and reference resolution within the framework.
 */

public class ObjectRepository {
    Map<String, Object> objectNameToInstanceMap = new HashMap<>(); 
    Map<Object, String> instanceToObjectNameMap = new HashMap<>(); 
    Map<String, Set<String>> objectsBySchema = new HashMap<>(); 

    
    /**
     * Stores an object instance in the repository.
     *
     * @param objectName name assigned to the object
     * @param schemaName schema under which the object was created
     * @param objectInstance runtime object instance
     */

    public void storeObject(String objectName, String schemaName, Object objectInstance){
        objectNameToInstanceMap.put(objectName, objectInstance); 
        instanceToObjectNameMap.put(objectInstance, objectName); 
        addToMapCollection(objectsBySchema, schemaName, objectName); 
    }

    
    /**
     * Small AI-generated Helper method for adding a value to a map-of-sets structure.
     * <p>
     * This ensures a set is created if one does not exist.
     *
     * @param map map of keys to value sets
     * @param key key to insert under
     * @param value value to add
     */

    public static <K, V> void addToMapCollection(Map<K, Set<V>> map, K key, V value) {
        map.computeIfAbsent(key, k -> new HashSet<>()).add(value);
    }

    
    /**
     * Ensures that an object name is valid and unique.
     *
     * @param objectName proposed object name
     * @throws ObjectException if the name is empty, contains commas,
     *                        or already exists
     */

    public void noObjectDuplicates(String objectName) throws ObjectException{
        if (objectName.isEmpty()){
            throw new ObjectException("Object must have a name"); 
        }

        if (objectName.contains(",")){
            throw new ObjectException("Object name can't contain comma"); 
        }

        if (objectNameToInstanceMap.containsKey(objectName)){
            throw new ObjectException("Duplicate object name found"); 
        } 
    }

    
    /**
     * Retrieves an object instance by name.
     *
     * @param objectName name of the object
     * @return object instance or {@code null} if not found
     */

    public Object getObject(String objectName){
        return objectNameToInstanceMap.get(objectName);
    }

    
    /**
     * Retrieves the name associated with a given object instance.
     *
     * @param objectInstance object instance
     * @return object name or {@code null} if not registered
     */

    public String getObjectName(Object objectInstance){
        return instanceToObjectNameMap.get(objectInstance);
    }

    
    /**
     * Returns all objects created under a specific schema.
     *
     * @param schemaName name of the schema
     * @return set of object instances (empty if none exist)
     */

    public Set<Object> getSchemaObjects(String schemaName){
        Set<String> objectNames = objectsBySchema.get(schemaName); 
        // else if is technically redundant/not needed, but would catch any future implementation that does
        // objectsBySchema.put(schemaName, new HashSet<>()); or anything equivalent. 
        if (objectNames == null ){
            return new HashSet<>(); 
        } else if (objectsBySchema.get(schemaName).isEmpty()) {
            return new HashSet<>(); 
        }
        else {
            Set<Object> objects = new HashSet<>(); 
            for (String objectName : objectNames){
                objects.add(objectNameToInstanceMap.get(objectName)); 
            }
            return objects; 
        }
    }

    
    /**
     * Exposes the internal instance-to-name mapping.
     * <p>
     * Primarily used by the {@link framework.runtime.ObjectViewer}
     * for display purposes.
     *
     * @return instance-to-name map
     */

    public Map<Object, String> getInstanceToObjectNameMap() {
        return instanceToObjectNameMap;
    }
}
