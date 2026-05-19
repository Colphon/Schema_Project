package framework.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException; 
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList; 
import java.util.Map;

import framework.schema.SchemaDefinition;
import framework.schema.SchemaException;
import framework.schema.SchemaRegistry; 


/**
 * Responsible for constructing runtime object instances from schemas.
 * <p>
 * {@code ObjectFactory}:
 * <ul>
 *   <li>Creates objects using reflection</li>
 *   <li>Initializes constructor parameters with safe defaults</li>
 *   <li>Assigns schema-controlled fields post-construction</li>
 * </ul>
 * Constructor safety is enforced by schema validation prior to use.
 */

public class ObjectFactory {
    SchemaRegistry registry; 
    ObjectRepository repository; 
    // Could add more defaults for more functionality, but not needed for baseline demonstration. 
    final Integer defaultInteger = 0;
    final Double defaultDouble = 0.0;
    final String defaultString = ""; 
    final Boolean defaultBoolean = false;
    
    
    /**
     * Constructs an object factory.
     *
     * @param registry schema registry
     * @param repository object repository
     */

    public ObjectFactory(SchemaRegistry registry, ObjectRepository repository){
        this.repository = repository; 
        this.registry = registry; 
    }

    
    /**
     * Creates and stores an object instance based on a schema definition.
     *
     * @param objectSchema schema definition
     * @param objectName user-defined object name
     * @param fieldMap parsed field values
     * @throws SchemaException if the base class is invalid
     * @throws ObjectException if object creation fails
     */

    public void createObject(SchemaDefinition objectSchema, String objectName, Map<String, Object> fieldMap) throws SchemaException, ObjectException{
        Class<?> baseClass = registry.findBaseClass(objectSchema.getBase());
        Constructor<?>[] constructList = baseClass.getDeclaredConstructors();
        Constructor<?> baseConstructor = constructList[0]; 
        Parameter[] parameters = baseConstructor.getParameters(); 

        // Instantiate object using constructor here. Could have multiple constructors in file, 
        // but making code to discern that is not needed for base functionality. 
        
        Object[] paramValues = new Object[parameters.length]; 
        for (int index = 0; index < parameters.length; index++){
            Parameter param = parameters[index]; 
            Type paramType = param.getParameterizedType(); 
            Class <?> paramObject = param.getType(); 
            if (paramType instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) paramType;
                Type raw = pType.getRawType(); 
                Class<?> rawClass = (Class<?>) raw;
                if (rawClass.equals(ArrayList.class)){
                    paramValues[index] = new ArrayList<>(); 
                } else {
                    throw new ObjectException("This type of Collection is not supported"); 
                }
            } else if (paramObject.equals(int.class) || paramObject.equals(Integer.class)){
                paramValues[index] = defaultInteger; 
            } else if (paramObject.equals(double.class) || paramObject.equals(Double.class)) {
                paramValues[index] = defaultDouble; 
            } else if (paramObject.equals(boolean.class) || paramObject.equals(Boolean.class)) {
                paramValues[index] = defaultBoolean; 
            } else if (paramObject.equals(String.class)){
                paramValues[index] = defaultString; 
            } else {
                paramValues[index] = null; 
            }
        }
        

        try {
            baseConstructor.setAccessible(true);
            Object objectInstance = baseConstructor.newInstance(paramValues); 
            for (String feld : objectSchema.getFieldList()){
                Field field = baseClass.getDeclaredField(feld);
                field.setAccessible(true);
                field.set(objectInstance, fieldMap.get(feld)); 
            }
            repository.storeObject(objectName, objectSchema.getSchemaName(), objectInstance); 
        } catch (NoSuchFieldException e) {
            throw new ObjectException("Runtime did not find field (Schema validation error)"); 
        }
        catch (IllegalAccessException e){
            throw new ObjectException("Runtime failed to assign value"); 
        }
        catch (InstantiationException e){
            throw new ObjectException("Runtime found class that can't be instantiated (Schema validation error)");
        }
        catch (InvocationTargetException e){
            throw new ObjectException("Error created by constructor failure (constructor could rely on parameter for logic)"); 
        }
        
    }
}
