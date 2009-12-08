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

    private enum Color { NONE, WHITE, RED, GREEN };
    
    public static final ReturnValue SUCCESS = new ReturnValue(Color.GREEN);
    public static final ReturnValue PENDING = new ReturnValue(Color.NONE);
    public static final ReturnValue FAILURE = new ReturnValue(Color.RED);
    public static final ReturnValue SKIPPED = new ReturnValue(Color.WHITE);
    
    private final Color color;
    private final Object returnValue;
    private final Object testCaseInstance;

    public ReturnValue(Object returnValue, Object testCaseInstance) {
        assert testCaseInstance != null;
        this.color = Color.GREEN;
        this.returnValue = returnValue;
        this.testCaseInstance = testCaseInstance;
    }
    
    private ReturnValue(Color color) {
        this.color = color;
        this.returnValue = this.testCaseInstance = null;
    }

    public ReturnValue withoutCache() {
        return this.testCaseInstance == null ? this : SUCCESS;
    }

    public Object getTestCaseInstance() {
        return testCaseInstance;
    }

    public Object getValue() {
        return returnValue;
    }

    public boolean isGreen() {
        return this == SUCCESS || this.testCaseInstance != null;
    }

    public boolean isMissing() {
        return this == PENDING || this == SUCCESS;
    }    

    public boolean isTestCaseInstanceOf(Class<?> jclass) {
        return testCaseInstance != null && testCaseInstance.getClass() == jclass;
    }
     
    public boolean hasBeenRun() {
        return this != PENDING;
    }
    
}
