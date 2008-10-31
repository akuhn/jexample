package jexample.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import jexample.JExampleOptions;

public class Util {

    public static Constructor getConstructor(Class jClass) throws SecurityException, NoSuchMethodException {
        if (!Modifier.isPublic(jClass.getModifiers())) {
            Constructor $ = jClass.getDeclaredConstructor();
            $.setAccessible(true);
            return $;
        }
        return jClass.getConstructor();
    }

    public static boolean cloneReturnValue(JExampleOptions $) {
        return $ != null && $.cloneReturnValues();
    }
    
    public static boolean cloneTestCase(JExampleOptions $) {
        return $ != null && $.cloneTestCase();
    }
    
    public static <T> T getField(Object $, String name) {
        try {
            Field f = $.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return (T) f.get($);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static <T> T forceClone(T $) throws InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
        if (isImmutable($)) return $;
        if (isCloneable($)) return clone($);
        Class<?> jclass = $.getClass();
        Object clone = Util.getConstructor(jclass).newInstance();
        for ( Field field : jclass.getDeclaredFields()) {
            if (Modifier.isFinal(field.getModifiers())) continue;
            if (Modifier.isStatic(field.getModifiers())) continue;
            field.setAccessible(true);
            field.set(clone, forceClone(field.get($)));
        }
        return (T) clone;
    }
    
    public static boolean isImmutable(Object $) {
        return $ == null
            || $ instanceof String
            || $ instanceof Number // assume all subclasses are immutable
            || $ instanceof Boolean;
            // TODO add more classes
    }
    
    public static boolean isCloneable(Object $) {
        if ($ == null) return true;
        if (!($ instanceof Cloneable)) return false;
        try {
            // False friend: the interface Cloneable does not specify the method 
            // clone, it is a tagging interface only, hence we must check here
            // if #clone is actually implemented!
            $.getClass().getMethod("clone");
        } catch (SecurityException ex) {
            return false;
        } catch (NoSuchMethodException ex) {
            return false;
        }
        return true;
    }
       
    public static <T> T clone(T $) {
        try {
            Method cloneMethod = $.getClass().getMethod("clone");
            return (T) cloneMethod.invoke($);
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
    
    
}
