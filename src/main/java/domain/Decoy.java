package domain;
import annotations.SchemaEligible; 
@SchemaEligible 


/**
 * A deliberately constructed class used to test and demonstrate
 * schema-eligibility constraints in the framework.
 * <p>
 * This class exists to validate rules such as:
 * <ul>
 *   <li>Static fields must not be exposed through schemas</li>
 *   <li>Constructors must not rely on schema-mutable field values</li>
 *   <li>Runtime field mutation must be schema-controlled</li>
 * </ul>
 */

public class Decoy {
    // This is rejected and not displayed. 
    static String DecoyString = "Evil static field..."; 
    protected double addNum = 1.1; 
    private double storeNum = 2; 

    
    /**
     * Constructs a {@code Decoy} instance using only constructor parameters
     * and internal state.
     * <p>
     * This constructor is valid because it does not rely on the initial
     * values of schema-mutable fields.
     *
     * @param addNum value added to the internal store
     */

    public Decoy (Integer addNum){
        storeNum += addNum; 
    }

    /* This is not valid, however. 
    public Decoy (Integer addNum){
        this.addNum = addNum; 
        storeNum += this.addNum; 
    }
    */

    /* This is not valid as well. 
    public Decoy (){
        addNum += addNum; 
    }
    */
}
