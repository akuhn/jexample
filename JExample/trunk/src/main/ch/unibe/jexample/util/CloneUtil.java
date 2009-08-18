package ch.unibe.jexample.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import ch.unibe.jexample.deepclone.CloneFactory;

public class CloneUtil {

    public static long NANOS;

    public static Constructor<?> getConstructor(Class<?> jClass) throws SecurityException, NoSuchMethodException {
        if (!Modifier.isPublic(jClass.getModifiers())) {
            Constructor<?> $ = jClass.getDeclaredConstructor();
            $.setAccessible(true);
            return $;
        }
        return jClass.getConstructor();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Object object, String name) {
        try {
            Field f = object.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return (T) f.get(object);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T forceClone(T object) {
        long time = System.nanoTime();
        try {
            return new CloneFactory().clone(object);
        }
        finally {
            NANOS += (System.nanoTime() - time);
        }
    }

}
