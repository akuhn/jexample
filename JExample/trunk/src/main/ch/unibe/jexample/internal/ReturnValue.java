package ch.unibe.jexample.internal;


/** 
 * 
 * @author Adrian Kuhn
 * 
 */
public class ReturnValue {

    public static final ReturnValue R_GREEN = new ReturnValue();
    public static final ReturnValue R_NONE = new ReturnValue();
    public static final ReturnValue R_RED = new ReturnValue();
    public static final ReturnValue R_WHITE = new ReturnValue();
    
    private Object returnValue;
    private Object testCaseInstance;

    public ReturnValue(Object returnValue, Object testCaseInstance) {
        assert testCaseInstance != null;
        this.returnValue = returnValue;
        this.testCaseInstance = testCaseInstance;
    }
    
    private ReturnValue() {
        // do nothing
    }

    public void dispose() {
        returnValue = null;
        testCaseInstance = null;
    }

    public Object getTestCaseInstance() {
        return testCaseInstance;
    }

    public Object getValue() {
        return returnValue;
    }

    public boolean isGreen() {
        return this == R_GREEN || this.testCaseInstance != null;
    }

    public boolean isNull() {
        return this == R_NONE;
    }    

    public boolean isTestCaseInstanceOf(Class<?> jclass) {
        return testCaseInstance != null && testCaseInstance.getClass() == jclass;
    }
     
}
