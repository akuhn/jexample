package jexample.internal;

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
        return returnValue instanceof String;
        // TODO add more here ... maybe keep a list of classes somewhere
    }

    private static boolean keep(InjectionPolicy policy) {
        return policy != null && policy.keep();
    }

    void assign(Object value) {
        this.returnValue = value;
    }
    
}
