import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import annotations.SchemaEligible;
import framework.runtime.ObjectException;
import framework.runtime.ObjectRepository;
import framework.schema.SchemaException;
import framework.schema.SchemaRegistry;
import framework.schema.SchemaValidator; 



public class PTest {
    @Test
    public void findBaseClassTest(){
        Reflections domainScan = new Reflections("domain");
        Set<Class<?>> classes = domainScan.getTypesAnnotatedWith(SchemaEligible.class);
        SchemaRegistry registry = new SchemaRegistry(classes);
        Exception exception = assertThrows(SchemaException.class, () -> {
        registry.findBaseClass("Stupent"); 
        });
    }

    @Test
    public void validateEmptyParseValueTest(){
        Reflections domainScan = new Reflections("domain");
        Set<Class<?>> classes = domainScan.getTypesAnnotatedWith(SchemaEligible.class);
        SchemaRegistry registry = new SchemaRegistry(classes);
        ObjectRepository repository = new ObjectRepository(); 
        SchemaValidator validator = new SchemaValidator(registry, repository); 
        Exception intException = assertThrows(ObjectException.class, () -> {
        validator.validateParseValue("Section", "capacity", ""); 
        });
        Exception boolException = assertThrows(ObjectException.class, () -> {
        validator.validateParseValue("Section", "available", ""); 
        });

        assertDoesNotThrow(() -> {
        validator.validateParseValue("Student", "studentId", ""); 
        });

        assertDoesNotThrow(() -> {
        validator.validateParseValue("Student", "sections", ""); 
        });
    }

    @Test
    public void validateSchemaTest(){
        Reflections domainScan = new Reflections("domain");
        Set<Class<?>> classes = domainScan.getTypesAnnotatedWith(SchemaEligible.class);
        SchemaRegistry registry = new SchemaRegistry(classes);
        ObjectRepository repository = new ObjectRepository(); 
        SchemaValidator validator = new SchemaValidator(registry, repository); 
        
        ArrayList<String> fields = new ArrayList<>(); 
        assertDoesNotThrow(() -> {
        validator.validateSchema("Student", fields); 
        });
        fields.add("studentId"); 
        assertDoesNotThrow(() -> {
        validator.validateSchema("Student", fields); 
        });
        fields.add("sections"); 
        assertDoesNotThrow(() -> {
        validator.validateSchema("Student", fields); 
        });
        fields.add(""); 
        Exception exception = assertThrows(SchemaException.class, () -> {
        validator.validateSchema("Student", fields); 
        });
    }
}
