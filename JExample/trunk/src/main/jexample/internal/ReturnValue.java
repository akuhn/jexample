package jexample.internal;

import static jexample.internal.Util.forceClone;
import static jexample.internal.Util.isCloneable;
import static jexample.internal.Util.isImmutable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jexample.JExampleOptions;

/**
 * 
 * @author Adrian Kuhn
 *
 */
public class ReturnValue {

    public final Example provider;
    private Object returnValue;
    private Object testCaseInstance;

    public ReturnValue(Example provider) {
        this.provider = provider;
    }
    
    
    public Object getValue() {
        return returnValue;
    }
    
    public Object get(JExampleOptions options) throws Exception {
        if (isImmutable(returnValue)) 
            return returnValue;
        if (!options.cloneReturnValues()) 
            return returnValue;
        if (isCloneable(returnValue)) 
            return Util.clone(returnValue);
        return provider.reRunTestMethod();
    }
    
    void assign(Object value) {
        this.returnValue = value;
    }

    public Object getTestCaseInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
        return forceClone(testCaseInstance);
    }

    public void assignInstance( Object test ) {
        assert this.provider.owner.jclass.equals( test.getClass() );
        this.testCaseInstance = test;
    }

    public boolean hasTestCaseInstance(Class<?> jclass) {
        return testCaseInstance != null && testCaseInstance.getClass() == jclass;
    }
    
}
