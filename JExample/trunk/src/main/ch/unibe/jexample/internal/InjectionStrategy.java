package ch.unibe.jexample.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ch.unibe.jexample.internal.deepcopy.CloneFactory;

public interface InjectionStrategy {

    public static final Object MISSING = new Object();
    
    public InjectionValues makeInjectionValues(Object receiver, Object[] arguments);

}

class RerunInjectionStrategy implements InjectionStrategy {

    @Override
    public InjectionValues makeInjectionValues(Object receiver, Object[] args) {
        for (int n = 0; n < args.length; n++) args[n] = MISSING;
        return new InjectionValues(MISSING, args);
    }
    
}

class NoneInjectionStrategy implements InjectionStrategy {
    
    @Override
    public InjectionValues makeInjectionValues(Object receiver, Object[] args) {
        return new InjectionValues(receiver, args);
    }

}

class DeepcopyInjectionStrategy implements InjectionStrategy {
    
    @Override
    public InjectionValues makeInjectionValues(Object receiver, Object[] args) {
        CloneFactory f = new CloneFactory();
        return new InjectionValues(f.clone(receiver), f.clone(args));
    }

}

class CloneInjectionStrategy implements InjectionStrategy {
    
    private static final Method OBJECT_CLONE = initializeCloneMethod();
    
    @Override
    public InjectionValues makeInjectionValues(Object receiver, Object[] args) {
        for (int n = 0; n < args.length; n++) args[n] = cloneArgument(args[n]);
        return new InjectionValues(cloneReceiver(receiver), args);
    }

    private Object cloneReceiver(Object receiver) {
        return MISSING;
    }

    private Object cloneArgument(Object object) {
        if (!(object instanceof Cloneable)) return MISSING;
        try {
            return OBJECT_CLONE.invoke(object);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessError(ex.getMessage());
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    private static Method initializeCloneMethod() {
        try {
            Method m = Object.class.getDeclaredMethod("clone");
            m.setAccessible(true);
            return m;
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchMethodException ex) {
            throw new NoSuchMethodError(ex.getMessage());
        }
    }

}