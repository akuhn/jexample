package ch.unibe.jexample.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.runner.Description;

public class MethodReference {

    private Method jmethod;
    private final Class<?> jclass;
    private Throwable error;

    public MethodReference(Class<?> jclass, Method jmethod) {
        assert jmethod.getDeclaringClass().isAssignableFrom(jclass);
        jmethod.setAccessible(true);
        this.jmethod = jmethod;
        this.jclass = jclass;
    }

    public MethodReference(Throwable ex) {
        this.error = ex;
        this.jclass = null;
    }
    
    public boolean isBroken() {
        return error != null;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof MethodReference) &&
                ((MethodReference) other).jmethod.equals(jmethod) &&
                ((MethodReference) other).getActualClass().equals(getActualClass());
    }

    @Override
    public int hashCode() {
        return jmethod.hashCode() ^ getActualClass().hashCode();
    }

    public static Collection<MethodReference> all(Class<?> jclass) {
        Collection<MethodReference> all = new ArrayList<MethodReference>();
        for (Method m: jclass.getMethods())
            all.add(new MethodReference(jclass, m));
        return all;
    }

    public static Collection<MethodReference> all(Class<?> jclass, Class<? extends Annotation> anon) {
        Collection<MethodReference> all = new ArrayList<MethodReference>();
        for (Method m: jclass.getMethods())
            if (m.isAnnotationPresent(anon)) all.add(new MethodReference(jclass, m));
        return all;
    }

    public Description createTestDescription() {
        return Description.createTestDescription(getActualClass(), getName());
    }

    public boolean equals(Class<?> c, String name) {
        return getActualClass() == c && getName().equals(name);
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return jmethod.isAnnotationPresent(annotationClass);
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return jmethod.getAnnotation(annotationClass);
    }

    public Object invoke(Object receiver, Object[] args) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
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
        if (isBroken()) return "Broken: " + error;
        return getActualClass().toString() + "#" + getName();
    }

    public boolean exists() {
        return true;
    }

    public Throwable getError() {
        assert isBroken();
        return error;
    }

    public Class<?> getActualClass() {
        return jclass;
    }

}
