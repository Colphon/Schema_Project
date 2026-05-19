package framework.cli;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import framework.runtime.ObjectException;
import framework.runtime.ObjectFactory;
import framework.runtime.ObjectRepository;
import framework.runtime.ObjectViewer;
import framework.schema.SchemaDefinition;
import framework.schema.SchemaException;
import framework.schema.SchemaRegistry;
import framework.schema.SchemaValidator;

/**
 * Command-line interface controller for the schema-driven framework.
 * <p>
 * This class:
 * <ul>
 *   <li>Handles user input and menu navigation</li>
 *   <li>Coordinates schema creation and object instantiation</li>
 *   <li>Delegates validation, construction, and display logic to core framework components</li>
 * </ul>
 * The controller intentionally avoids reflection and business logic, serving
 * purely as an orchestration layer.
 */

public class CLIController {
    SchemaRegistry registry; 
    SchemaValidator validator; 
    ObjectFactory factory; 
    ObjectRepository repository; 
    ObjectViewer viewer; 

    
    /**
     * Constructs and immediately launches the CLI controller.
     *
     * @param registry schema registry
     * @param validator schema and value validator
     * @param factory object factory
     * @param repository object repository
     * @param viewer schema-aware object viewer
     */

    public CLIController(SchemaRegistry registry, SchemaValidator validator, ObjectFactory factory, ObjectRepository repository, ObjectViewer viewer){
        this.registry = registry; 
        this.validator = validator; 
        this.factory = factory; 
        this.repository = repository; 
        this.viewer = viewer;
        printBaseClasses(); 
        actionLoop(); 
    }

    
    /**
     * Displays all discovered schema-eligible base classes to the user.
     */

    private void printBaseClasses(){
        Set<Class<?>> classes = registry.getClasses(); 
        System.out.println("Discovered schema-eligible base classes:"); 
        for (Class<?> clazz : classes){
            System.out.println("- " + clazz.getSimpleName()); 
        }
    }

    
    /**
     * Main interactive loop handling all user actions.
     * <p>
     * Supported operations include:
     * <ul>
     *   <li>Defining schemas</li>
     *   <li>Creating objects from schemas</li>
     *   <li>Listing schemas and schema objects</li>
     *   <li>Exiting the program</li>
     * </ul>
     */

    private void actionLoop(){
        int choice = 0;  
        Scanner scanner = new Scanner(System.in);
        while (choice != 4){
            System.out.println("Choose an action:\n1) Define new schema\n2) Create object from schema");
            System.out.println("3) List schemas and objects\n4) Exit");
            try {
                choice = Integer.parseInt(scanner.nextLine()); 
            } catch (NumberFormatException e){
                System.out.println("Please choose an option 1-4");
                continue; 
            }
            switch (choice) {
                case 1:
                    try{
                        // Don't need to follow java identifier rules for schema/object names.
                        // They are runtime identifiers. Only commas in object names are disallowed. 
                        // This prevents issues with collection parsing since the delimeter is ,.  
                        System.out.print("Define New Schema\nEnter schema name: ");
                        String schema = scanner.nextLine().trim(); 
                        registry.noDuplicates(schema);
                        // Abstract classes and interfaces could work as field types or references, but don't make sense as base classes. 
                        // They don't need to be implemented in this baseline program though. 
                        // Compatability with multiple constructors could also be implemented, but not needed for this project. 
                        System.out.println("Base class can only have one constructor"); 
                        System.out.println("Base class can't rely on schema-mutable field values during construction");
                        System.out.println("Base class should not be an interface or abstract class"); 
                        System.out.print("Select base class: ");
                        String base = scanner.nextLine().trim(); 
                        registry.findBaseClass(base);
                        String fieldDisplay = validator.displayBaseClass(base, Optional.empty()); 
                        System.out.print(fieldDisplay); 
                        // Could use aliasing to make user-inputted field names, but not needed for this baseline program. 
                        System.out.print("Select schema fields separated with commas: "); 
                        String fieldsubList = scanner.nextLine(); 
                        ArrayList<String> fieldList; 
                        if (fieldsubList.trim().isEmpty()){
                            fieldList = new ArrayList<>(); 
                        } else {
                            String[] arr = fieldsubList.replaceAll("\\s", "").split(","); 
                            fieldList = new ArrayList<>(Arrays.asList(arr));
                        }
                        validator.validateSchema(base, fieldList); 
                        SchemaDefinition definition = new SchemaDefinition(schema, base, fieldList);
                        registry.addSchema(definition);
                        System.out.println("Schema " + schema + " defined successfully"); 
                    } catch (SchemaException e) {
                        System.out.println(e.getMessage()); 
                    }
                    break; 
                case 2:
                    try{
                        System.out.println("Create Object from Schema\nAvailable schemas:");
                        for (SchemaDefinition schemaDefinition : registry.getSchemas()){
                            System.out.println("- " + schemaDefinition.getSchemaName()); 
                        }
                        System.out.print("Select schema: ");
                        String schemaPick = scanner.nextLine(); 
                        SchemaDefinition objectSchema = registry.findSchema(schemaPick);
                        ArrayList<String> fields = objectSchema.getFieldList(); 
                        System.out.print("Select object name: "); 
                        String objectName = scanner.nextLine().trim(); 
                        repository.noObjectDuplicates(objectName); 
                        Map<String, Object> parsedFieldValues = new HashMap<>();
                        if (fields.isEmpty() || fields.get(0) == ""){
                            factory.createObject(objectSchema, objectName, parsedFieldValues);
                            System.out.println("Object created: " + schemaPick);
                        } else {
                            System.out.println("Enter values (Separate collection values with commas):");
                            for (String field : fields){
                                System.out.print(field + ": ");
                                String value = scanner.nextLine(); 
                                Object parsedFieldValue = validator.validateParseValue(objectSchema.getBase(), field, value); 
                                parsedFieldValues.put(field, parsedFieldValue); 
                            }
                            factory.createObject(objectSchema, objectName, parsedFieldValues); 
                            System.out.println("Object created: " + objectName);
                        }
                    } catch (SchemaException | ObjectException e) {
                        System.out.println(e.getMessage()); 
                    }
                    break;
                case 3:
                    try {
                        for (SchemaDefinition schema : registry.getSchemas()){
                            System.out.println("\n"); 
                            System.out.println("Schema " + schema.getSchemaName()); 
                            System.out.println("Base class: " + schema.getBase());
                            if (!(schema.getFieldList().size() == 0)){
                                System.out.println("Schema Fields:");
                            }
                            String schemaFieldDisplay = validator.displayBaseClass(schema.getBase(), Optional.of(schema.getFieldList())); 
                            System.out.print(schemaFieldDisplay); 
                            Set<Object> schemaObjects = repository.getSchemaObjects(schema.getSchemaName()); 
                            if (!schemaObjects.isEmpty()){
                                System.out.println("Schema Objects:\n"); 
                                for (Object schemaObject : schemaObjects){
                                    System.out.println(viewer.displaySchemaObject(schemaObject, schema));
                                }
                            }
                        }
                    } catch (SchemaException | ObjectException e) {
                        System.out.println(e.getMessage());
                    }
                    
                    break;
                case 4:
                    break;
                default:
                    System.out.println("Please choose an option 1-4");
                    break; 
            }
        }
        scanner.close(); 
    }
}
