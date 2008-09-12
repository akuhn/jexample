package jexample.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MethodReference {

    private final Method jmethod;
    public final Class<?> jclass;
    
    public MethodReference(Class<?> klass, Method method) {
        assert method.getDeclaringClass().isAssignableFrom(klass);
        method.setAccessible(true);
        this.jmethod = method;
        this.jclass = klass;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof MethodReference) 
                && ((MethodReference) o).jmethod.equals(jmethod)
                && ((MethodReference) o).jclass.equals(jclass);
    }

    @Override
    public int hashCode() {
        return jmethod.hashCode() ^ jclass.hashCode();
    }
    
    public static Collection<MethodReference> all(Class<?> klass) {
        Collection<MethodReference> $ = new ArrayList();    
        for (Method m : klass.getMethods()) {
            $.add(new MethodReference(klass, m));
        }
        return $;
    }
    
    public boolean equals(Class<?> c, String name) {
        return jclass == c && getName().equals(name);
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return jmethod.isAnnotationPresent(annotationClass);
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return jmethod.getAnnotation(annotationClass);
    }

    public Object invoke(Object receiver, Object[] args) 
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return jmethod.invoke(receiver, args);
    }

    public int arity() {
        return jmethod.getParameterTypes().length;
    }

    public Class<?>[] getParameterTypes() {
        return jmethod.getParameterTypes();
    }

    public Class<?> getReturnType() {
        return jmethod.getReturnType();
    }

    public String getName() {
        return jmethod.getName();
    }
    
    @Override
    public String toString() {
        return jclass.toString() + "#" + getName();
    }
    
}
