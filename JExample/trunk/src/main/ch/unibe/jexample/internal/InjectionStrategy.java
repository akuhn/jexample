package ch.unibe.jexample.internal;

import static ch.unibe.jexample.internal.ReturnValue.MISSING;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import ch.unibe.jexample.internal.deepcopy.CloneFactory;
import ch.unibe.jexample.internal.deepcopy.ImmutableClasses;
import ch.unibe.jexample.internal.util.Reflection;

public interface InjectionStrategy {

    public InjectionValues cloneInjectionValues(Object receiver, Object... arguments);

}

class RerunInjectionStrategy implements InjectionStrategy {

    @Override
    public InjectionValues cloneInjectionValues(Object receiver, Object... args) {
        for (int n = 0; n < args.length; n++) args[n] = MISSING;
        return new InjectionValues(MISSING, args);
    }
    
}

class NoneInjectionStrategy implements InjectionStrategy {
    
    @Override
    public InjectionValues cloneInjectionValues(Object receiver, Object... args) {
        return new InjectionValues(receiver, args);
    }

}

class DeepcopyInjectionStrategy implements InjectionStrategy {
    
    private CloneFactory factory = new CloneFactory();

    @Override
    public InjectionValues cloneInjectionValues(Object receiver, Object... args) {
        return new InjectionValues(deepcopy(receiver), deepcopy(args));
    }
    
    private <T> T deepcopy(T object) {
        return factory.clone(object);
    }

}

class CloneInjectionStrategy implements InjectionStrategy {
    
    private static final Method OBJECT_CLONE = initializeCloneMethod();
    
    @Override
    public InjectionValues cloneInjectionValues(Object receiver, Object... args) {
        for (int n = 0; n < args.length; n++) args[n] = cloneArgument(args[n]);
        return new InjectionValues(cloneReceiver(receiver), args);
    }
    
    private Object cloneReceiver(Object receiver) {
        if (receiver == null) return null;
        try {
            Object clone = Reflection.newInstance(receiver.getClass());
            for (Field f: receiver.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;
                if (Modifier.isFinal(f.getModifiers())) continue;
                f.setAccessible(true);
                Object value = f.get(receiver);
                if (value == null || ImmutableClasses.contains(value.getClass())) {
                    f.set(clone, value);
                }
                else {
                    if (!(Cloneable.class.isAssignableFrom(value.getClass()))) return MISSING;
                    f.set(clone, Reflection.invoke(OBJECT_CLONE, value));
                }
            }
            return clone;
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } 
    }
    
    private Object cloneArgument(Object object) {
        if (object == null) return null;
        if (ImmutableClasses.contains(object.getClass())) return object;
        if (!(object instanceof Cloneable)) return MISSING;
        return Reflection.invoke(OBJECT_CLONE, object);
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