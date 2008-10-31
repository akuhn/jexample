package jexample.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jexample.InjectionPolicy;

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
    
    public boolean isCloneable() {
        if (returnValue == null) return true;
        if (!(returnValue instanceof Cloneable)) return false;
        try {
            returnValue.getClass().getMethod("clone");
        } catch (SecurityException ex) {
            return false;
        } catch (NoSuchMethodException ex) {
            return false;
        }
        return true;
    }
       
    public Object getClone() {
        try {
            Method cloneMethod = returnValue.getClass().getMethod("clone");
            return cloneMethod.invoke(returnValue);
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public Object getValue() {
        return returnValue;
    }
    
    public Object get(InjectionPolicy policy) throws Exception {
        if (returnValue == null) return null;
        if (isCloneable()) return getClone();
        if (isImmutable() || keep(policy)) return returnValue;
        returnValue = provider.reRunTestMethod();
        return returnValue;
    }
    
    private boolean isImmutable() {
        return returnValue instanceof String
            || returnValue instanceof Number // assume all subclasses are immutable
            || returnValue instanceof Boolean;
            // TODO add more classes
    }

    private static boolean keep(InjectionPolicy policy) {
        return policy != null && policy.keep();
    }

    void assign(Object value) {
        this.returnValue = value;
    }

    public Object getTestCaseInstance() throws InstantiationException, IllegalAccessException {
        return this.cloneTestCaseInstance();
    }

    private Object cloneTestCaseInstance() throws InstantiationException, IllegalAccessException {
        Class<? extends Object> clazz = this.testCaseInstance.getClass();
        Object clone = clazz.newInstance();
        
        Field[] fields = clazz.getFields();
        Field field;
        Object fieldValue;
        for ( int i = 0; i < fields.length; i++ ) {
            field = fields[i];
            fieldValue = field.get( this.testCaseInstance );
            field.set( clone , fieldValue );
        }
        
        return clone;
    }

    public void assignInstance( Object test ) {
        assert this.provider.owner.jclass.equals( test.getClass() );
        this.testCaseInstance = test;
    }

    public boolean hasTestCaseInstance() {
        return this.testCaseInstance != null;
    }
    
}
