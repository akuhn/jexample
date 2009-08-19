package ch.unibe.jexample.internal;


/** 
 * 
 * @author Adrian Kuhn
 * 
 */
public class ReturnValue {

    private Example provider;
    private Object returnValue;
    private Object testCaseInstance;
    ExampleColor color;

    public ReturnValue(Example provider) {
        this.color = ExampleColor.NONE;
        this.provider = provider;
    }

    public Object getValue() {
        return returnValue;
    }

    void assign(Object value) {
        this.returnValue = value;
    }

    void assignInstance(Object test) {
        assert this.provider.owner.getImplementingClass().equals(test.getClass());
        this.testCaseInstance = test;
    }

    public boolean isTestCaseInstanceOf(Class<?> jclass) {
        return testCaseInstance != null && testCaseInstance.getClass() == jclass;
    }

    public void dispose() {
        returnValue = null;
        testCaseInstance = null;
    }

    public Object getTestCaseInstance() {
        return testCaseInstance;
    }

}
