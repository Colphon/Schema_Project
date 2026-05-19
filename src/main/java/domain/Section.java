package domain;
import java.util.ArrayList;
 
import annotations.SchemaEligible; 


/**
 * Represents a specific section of a course.
 * <p>
 * A {@code Section} may optionally expose selected fields through schemas,
 * allowing multiple schema views to coexist over the same base class.
 * Fields not selected by a schema remain untouched at runtime.
 */

@SchemaEligible
public class Section {
    private String sectionId; 
    private Integer capacity;
    private Student topStudent; 
    private Boolean available; 
    private ArrayList<Section> subSections; 

    
    
    /**
     * Constructs a section with an identifier and capacity.
     * <p>
     * This constructor is schema-safe because it does not depend on
     * schema-mutable field values during execution.
     *
     * @param id section identifier
     * @param capacity maximum enrollment capacity
     */


    public Section(String id, Integer capacity){
        this.sectionId = id;
        this.capacity = capacity;
    }

    
    /**
     * Returns the section ID.
     *
     * @return section ID
     */

    public String getSectionId() {
        return sectionId;
    }
    
    /**
     * Returns the maximum enrollment capacity.
     *
     * @return capacity
     */

    public Integer getCapacity() {
        return capacity;
    }
}
