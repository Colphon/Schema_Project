package framework.schema;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier; 
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set; 

import framework.runtime.ObjectException; 
import framework.runtime.ObjectRepository; 


/**
 * Central validation component for schema and value semantics.
 * <p>
 * The {@code SchemaValidator} is responsible for:
 * <ul>
 *   <li>Validating schema definitions against base classes</li>
 *   <li>Ensuring schema field correctness</li>
 *   <li>Parsing and validating user-provided values</li>
 *   <li>Producing schema-aware field displays</li>
 * </ul>
 * This class contains the core semantic rules of the framework and
 * prevents invalid state from entering the runtime system.
 */

public class SchemaValidator{
    SchemaRegistry registry; 
    ObjectRepository repository; 
    
    /**
     * Constructs a schema validator.
     *
     * @param registry schema registry
     * @param repository object repository
     */

    public SchemaValidator(SchemaRegistry registry, ObjectRepository repository){
        this.registry = registry; 
        this.repository = repository; 
    }

    
    /**
     * Produces a formatted display of a base class's fields.
     * <p>
     * If a field list is provided, only those fields are displayed.
     * Otherwise, all eligible fields are shown.
     *
     * @param Base simple name of the base class
     * @param fieldList optional list of schema fields
     * @return formatted field description
     * @throws SchemaException if the base class cannot be found or inspected
     */

    public String displayBaseClass(String Base, Optional<ArrayList<String>> fieldList) throws SchemaException{
        String output = "";
        Class <?> baseClass = null; 
        Set<Class<?>> classes = registry.getClasses(); 
        for (Class<?> clazz : classes){
            if (clazz.getSimpleName().equals(Base)){
                baseClass = clazz; 
            }
        }

        if (baseClass == null){
            throw new SchemaException("Base class not found"); 
        }
        
        Field[] fields = baseClass.getDeclaredFields(); 
        
        for (Field feld : fields){

            // Code so that only schema Fields are displayed in option 3
            if (fieldList.isPresent()){
                boolean match = false; 
                for (String userField : fieldList.get()){
                    if (userField.equals(feld.getName())){
                        match = true; 
                    } 
                }
                if (!match){
                    continue; 
                }
            }
            int Modifiers = feld.getModifiers(); 
            
            if (Modifier.isStatic(Modifiers)){
                continue; 
            }
            // Transient, Synthetic, and volatile fields should technically also not be used/displayed. 

            if (Modifier.isPublic(Modifiers)){
                output += "+ "; 
            } else if (Modifier.isPrivate(Modifiers)){
                output += "- ";
            } else if (Modifier.isProtected(Modifiers)){
                output += "# ";
            } else {
                output += "~ ";
            }
            
    
            if (Modifier.isFinal(Modifiers)){
                output += "final "; 
            }

            output += feld.getName() + " : ";

            if (feld.getGenericType() instanceof ParameterizedType){
                // Currently can only display/use collections with concrete generic arguments

                ParameterizedType pType = (ParameterizedType) feld.getGenericType();
                Class<?> rawType = (Class<?>) pType.getRawType();
                String containerName = rawType.getSimpleName(); 
                
                
                if (pType.getActualTypeArguments()[0] instanceof Class<?>){
                    Type containerArg = pType.getActualTypeArguments()[0];
                    Class<?> argClass = (Class<?>) containerArg;
                    String ArgName = argClass.getSimpleName();
                    output += containerName + "<" + ArgName + ">"; 
                } else {
                    throw new SchemaException("Type Argument is not Class<?>"); 
                }
            } else if (feld.getGenericType() instanceof Class<?>) {
                output += feld.getType().getSimpleName();
            } else {
                throw new SchemaException("Runtime type is not Class<?> or ParameterizedType");
            }
            
            output += "\n"; 
            
        }
        return output; 
    }

    
    /**
     * Validates a proposed schema field list against a base class.
     *
     * @param Base base class name
     * @param fieldList list of schema fields
     * @throws SchemaException if schema rules are violated
     */

    public void validateSchema(String Base, ArrayList<String> fieldList) throws SchemaException{
        Class <?> baseClass = null; 
        Set<Class<?>> classes = registry.getClasses(); 
        for (Class<?> clazz : classes){
            if (clazz.getSimpleName().equals(Base)){
                baseClass = clazz; 
            }
        }

        if (baseClass == null){
            throw new SchemaException("Base class not found"); 
        }
        
        Field[] fields = baseClass.getDeclaredFields(); 
        Set <String> noDuplicates = new HashSet<>(); 
        
        for (String userField : fieldList){
            if (noDuplicates.contains(userField)){
                throw new SchemaException("No Duplicate Fields"); 
            } else {
                noDuplicates.add(userField); 
            }
            
            if (userField.equals("")){
                throw new SchemaException("A Field is \"\" "); 
            }
            boolean match = false; 
            for (Field feld : fields){
                if (userField.equals(feld.getName())){
                    int Modifiers = feld.getModifiers(); 
            
                    if (Modifier.isStatic(Modifiers)){
                        throw new SchemaException("Static field should not be used in Schema"); 
                    }

                    if (feld.getGenericType() instanceof ParameterizedType){
                        // Currently can only display/use collections with concrete generic arguments

                        ParameterizedType pType = (ParameterizedType) feld.getGenericType(); 
                        if (!(pType.getActualTypeArguments()[0] instanceof Class<?>)){
                            throw new SchemaException("Type Argument is not Class<?>");
                        }
                    } else if (feld.getGenericType() instanceof Class<?>) {
                    } else {
                        throw new SchemaException("Runtime type is not Class<?> or ParameterizedType");
                    }
                    match = true; 
                }
            }
            // Ensures empty lists are valid
            if (match){
            } else {
                throw new SchemaException("Field " + userField + " did not match any fields");
            }
        }
    }
    
    
    /**
     * Parses and validates a user-provided value for a schema field.
     *
     * @param Base base class name
     * @param fieldName schema field being assigned
     * @param value user input value
     * @return parsed value object
     * @throws ObjectException if validation or parsing fails
     */

    public Object validateParseValue(String Base, String fieldName, String value) throws ObjectException{
        Class <?> baseClass = null; 
        Set<Class<?>> classes = registry.getClasses(); 
        for (Class<?> clazz : classes){
            if (clazz.getSimpleName().equals(Base)){
                baseClass = clazz; 
            }
        }
        if (baseClass == null) {
            throw new ObjectException("Base class not found (while validating value");
        }

        try {
            Field feld = baseClass.getDeclaredField(fieldName);
            Class<?> feldType = feld.getType(); 
            if (value.isEmpty() && !(feld.getGenericType() instanceof ParameterizedType) && !(feldType.equals(String.class))){
                throw new ObjectException("Only Collections or Strings may contain empty input"); 
            }
            if (feld.getGenericType() instanceof ParameterizedType){
                // Uses collections with concrete generic arguments
                // Do when object code made. 

                ParameterizedType pType = (ParameterizedType) feld.getGenericType();

                if (!(pType.getActualTypeArguments()[0] instanceof Class)) {
                    throw new ObjectException("Wildcard generic types are not supported");
                }

                Class<?> elementType = (Class<?>) pType.getActualTypeArguments()[0];
                
                String[] objectNames = value.split(",");
                for (int i = 0; i < objectNames.length; i++) {
                    objectNames[i] = objectNames[i].trim();
                }

                Class<?> rawType = (Class<?>) pType.getRawType();
                if (rawType.equals(ArrayList.class)){

                    if (objectNames.length == 1 && objectNames[0].isEmpty()) {
                        return new ArrayList<>();
                    }

                    ArrayList<Object> valueArrayList = new ArrayList<>(); 
                    for (String objectName : objectNames){
                        Object elementObject = repository.getObject(objectName); 
                        if (elementObject == null){
                            throw new ObjectException("Object " + objectName + " does not exist"); 
                        }
                        if (elementType.isAssignableFrom(elementObject.getClass())){
                            valueArrayList.add(elementObject);
                        } else {
                            throw new ObjectException("Object type of " + objectName + " is not assignable to field type " + elementType.getSimpleName() + "\nCollection<thisObjectType>, but was provided object of otherObjectType");
                        }
                    }    
                    return valueArrayList; 
                } else {
                    throw new ObjectException("Collection type is not supported"); 
                }

            } else if (feldType.equals(int.class) || feldType.equals(Integer.class)) {

                try{
                    Integer intValue = Integer.parseInt(value); 
                    return intValue; 
                } catch (NumberFormatException e) {
                    throw new ObjectException("Input cannot be parsed into a integer"); 
                }

            } else if (feldType.equals(double.class) || feldType.equals(Double.class)) {

                try{
                    Double doubleValue = Double.parseDouble(value); 
                    return doubleValue; 
                } catch (NumberFormatException e) {
                    throw new ObjectException("Input cannot be parsed into a double"); 
                }

            } else if (feldType.equals(boolean.class) || feldType.equals(Boolean.class)) {
                if (!(value.toLowerCase().equals("true") || value.toLowerCase().equals("false"))){
                    throw new ObjectException("Boolean input should be true or false (case insensitive)");
                }
                Boolean BooleanValue = Boolean.parseBoolean(value); 
                return BooleanValue; 
            } else if (feldType.equals(String.class)){
                if (value.isEmpty()){
                    return ""; 
                } else {
                    return value; 
                }
            } else {
                if (repository.getObject(value) == null){
                    throw new ObjectException("Object does not exist"); 
                }
                if (feldType.isAssignableFrom(repository.getObject(value).getClass())){
                    return repository.getObject(value);
                } else {
                    throw new ObjectException("Object type is not assignable to field type");
                }
                
            }
        } catch (NoSuchFieldException e){
            throw new ObjectException("fieldName not found while validating value (This error should not occur naturally)"); 
        }
    }
}

