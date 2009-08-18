package ch.unibe.jexample.internal;

import static ch.unibe.jexample.util.CloneUtil.forceClone;
import ch.unibe.jexample.JExampleOptions;
import ch.unibe.jexample.util.CloneUtil;

/**
 * 
 * @author Adrian Kuhn
 * 
 */
public class ReturnValue {

    private Example provider;
    private Object returnValue;
    private Object testCaseInstance;
    public ExampleColor color;

    public ReturnValue(Example provider) {
        this.color = ExampleColor.NONE;
        this.provider = provider;
    }

    public Object getValue() {
        return returnValue;
    }

    public Object get(JExampleOptions options) throws Exception {
        if (hasSystemPropertyForceRerun()) return provider.bareInvoke();
        if (!options.cloneReturnValues()) return returnValue;
        return CloneUtil.forceClone(returnValue);
    }

    private boolean hasSystemPropertyForceRerun() {
        return false;
    }

    void assign(Object value) {
        this.returnValue = value;
    }

    public Object getTestCaseInstance() {
        return forceClone(testCaseInstance);
    }

    public void assignInstance(Object test) {
        assert this.provider.owner.getImplementingClass().equals(test.getClass());
        this.testCaseInstance = test;
    }

    public boolean hasTestCaseInstance(Class<?> jclass) {
        return testCaseInstance != null && testCaseInstance.getClass() == jclass;
    }

    public void dispose() {
        returnValue = null;
        testCaseInstance = null;
    }

}
