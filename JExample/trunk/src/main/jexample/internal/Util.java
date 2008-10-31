package jexample.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
    
    public static <T> T forceClone(T t) throws InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
        Class<?> jclass = t.getClass();
        Object clone = Util.getConstructor( jclass ).newInstance();
        for ( Field field : jclass.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get( t );
            field.set( clone , value );
        }
        return (T) clone;
    }
    
    
}
