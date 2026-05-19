package domain;
import java.util.ArrayList;

import annotations.SchemaEligible;
    
/**
 * Represents a student in the registrar system.
 * <p>
 * A {@code Student} may participate in multiple schemas, each exposing
 * different subsets of student data and relationships.
 */

@SchemaEligible
public class Student {
    private String studentId; 
    private String studentName;
    private ArrayList<Section> sections; 
    
    
    /**
     * Constructs a student with an identifier, name, and enrolled sections.
     * <p>
     * All schema-controlled fields are assigned externally through schemas
     * rather than relied upon during construction.
     *
     * @param id student identifier
     * @param name student name
     * @param sections list of enrolled sections
     */


    public Student(String id, String name, ArrayList<Section> sections){
        this.studentId = id;
        this.studentName = name;
        this.sections = sections;
    }

    
    /**
     * Returns the student ID.
     *
     * @return student ID
     */

    public String getStudentId() {
        return studentId;
    }

    
    /**
     * Returns the student name.
     *
     * @return student name
     */

    public String getStudentName() {
        return studentName;
    }

    
    /**
     * Returns the sections the student is enrolled in.
     *
     * @return list of sections
     */

    public ArrayList<Section> getSections() {
        return sections;
    }

    
    /**
     * Sets the student's enrolled sections.
     *
     * @param sections updated section list
     */

    public void setStudentSections(ArrayList<Section> sections) {
        this.sections = sections;
    }

    
    /**
     * Adds a section to the student's enrollment list.
     *
     * @param section section to add
     */

    public void addStudentSection(Section section){
        sections.add(section); 
    }
}
