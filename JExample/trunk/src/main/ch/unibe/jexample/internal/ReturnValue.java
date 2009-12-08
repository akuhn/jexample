package ch.unibe.jexample.internal;


/** Cached return value. A return value can be in one of five states, either it is
 * <UL><LI>pending (ie not yet run),<LI>a success with cached value,
 * <LI>a success without cached value<LI>a failure, or<LI>it has been skipped.</UL>
 * We say that a success has the color "green", and that a success without cached
 * value as well as pending values are "missing".  
 *<P>
 * Return values are immutable. 
 *<P>
 * @author Adrian Kuhn
 * 
 */
public class ReturnValue {

    public static final Object MISSING = new Object();
    
    public static final ReturnValue SUCCESS = new ReturnValue();
    public static final ReturnValue PENDING = new ReturnValue();
    public static final ReturnValue FAILURE = new ReturnValue();
    public static final ReturnValue SKIPPED = new ReturnValue();
    
    private final ReturnValue kind;
    private final Object returnValue;
    private final Object testCaseInstance;

    public ReturnValue(Object returnValue, Object testCaseInstance) {
        assert testCaseInstance != null;
        this.testCaseInstance = testCaseInstance;
        this.returnValue = returnValue;
        kind = SUCCESS;
    }
    
    private ReturnValue() {
        testCaseInstance = MISSING;
        returnValue = MISSING;
        kind = this;
    }

    public ReturnValue withoutCache() {
        return testCaseInstance == MISSING ? this : SUCCESS;
    }

    public Object getTestCaseInstance() {
        return testCaseInstance;
    }

    public Object getValue() {
        return returnValue;
    }

    public boolean isGreen() {
        return kind == SUCCESS;
    }

    public boolean isMissing() {
        return kind == PENDING || (kind == SUCCESS && testCaseInstance == MISSING);
    }    

    public boolean isTestCaseInstanceOf(Class<?> jclass) {
        return testCaseInstance != MISSING && testCaseInstance.getClass() == jclass;
    }
     
    public boolean hasBeenRun() {
        return kind != PENDING;
    }
    
    @Override
    public String toString() {
        if (kind == PENDING) return "PENDING";
        if (kind == FAILURE) return "FAILURE";
        if (kind == SKIPPED) return "SKIPPED";
        return "SUCCESS " + (testCaseInstance == MISSING ? "without" : "with") + " value";
    }
    
}
