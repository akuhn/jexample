package ch.unibe.jexample.internal.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.runner.Description;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.Injection;
import ch.unibe.jexample.InjectionPolicy;

public class MethodReference {

    public static Collection<MethodReference> all(Class<?> jclass) {
        Collection<MethodReference> all = new ArrayList<MethodReference>();
        for (Method m: jclass.getMethods())
            all.add(new MethodReference(jclass, m));
        return all;
    }
    private Throwable error;
    private final Class<?> jclass;

    private Method jmethod;

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

    public int arity() {
        return jmethod.getParameterTypes().length;
    }

    public Description createTestDescription() {
        return Description.createTestDescription(getActualClass(), getName());
    }

    public boolean equals(Class<?> c, String name) {
        return getActualClass() == c && getName().equals(name);
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof MethodReference) &&
                ((MethodReference) other).jmethod.equals(jmethod) &&
                ((MethodReference) other).getActualClass().equals(getActualClass());
    }

    public boolean exists() {
        return true;
    }

    public Class<?> getActualClass() {
        return jclass;
    }
    
    public String getDependencyString() {
        Given given = jmethod.getAnnotation(Given.class);
        return given == null ? "" : given.value(); 
    }

    public Throwable getError() {
        assert isBroken();
        return error;
    }

    public InjectionPolicy getInjectionPolicy() {
        Injection injection;
        // 1) method
        injection = jmethod.getAnnotation(Injection.class);
        if (declaresPolicy(injection)) return injection.value();
        // 2) class, then superclass chain
        for (Class<?> curr = jclass; curr != null; curr = curr.getSuperclass()) {
            injection = curr.getAnnotation(Injection.class);
            if (declaresPolicy(injection)) return injection.value();
        }
        // 3) package of class, than package of superclass chain
        for (Class<?> curr = jclass; curr != null; curr = curr.getSuperclass()) {
            Package pack = curr.getPackage();
            if (pack == null) continue;
            injection = pack.getAnnotation(Injection.class);
            if (declaresPolicy(injection)) return injection.value();
        }
        // 4) system property
        return getSystemInjectionPolicy();
    }

    public String getName() {
        return jmethod.getName();
    }

    public Class<?>[] getParameterTypes() {
        return jmethod.getParameterTypes();
    }

    public Class<?> getReturnType() {
        return jmethod.getReturnType();
    }

    @Override
    public int hashCode() {
        return jmethod.hashCode() ^ getActualClass().hashCode();
    }

    public Class<? extends Throwable> initExpectedException() {
        Test annotation = jmethod.getAnnotation(Test.class);
        if (annotation == null) return null;
        if (annotation.expected() == None.class) return null;
        return annotation.expected();
    }

    public Object invoke(Object receiver, Object[] args) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        return jmethod.invoke(receiver, args);
    }
    
    public boolean isBroken() {
        return error != null;
    }

    public boolean isIgnorePresent() {
        return jmethod.isAnnotationPresent(Ignore.class);
    }

    public boolean isTestAnnotationPresent() {
        return jmethod.isAnnotationPresent(Test.class)
            || jmethod.isAnnotationPresent(Given.class);
    }
    
    @Override
    public String toString() {
        if (isBroken()) return "Broken: " + error;
        return getActualClass().toString() + "#" + getName();
    }
    
    private boolean declaresPolicy(Injection injection) {
        return injection != null && injection.value() != InjectionPolicy.DEFAULT;
    }

    private InjectionPolicy getSystemInjectionPolicy() {
        String property = System.getProperty(InjectionPolicy.JEXAMPLE_INJECTION);
        if (property == null) return InjectionPolicy.DEFAULT;
        try {
            return InjectionPolicy.valueOf(property);
        }
        catch (IllegalArgumentException ex) {
            return InjectionPolicy.DEFAULT;
        }
    }

    public Iterable<MethodReference> collectDependencies() {
        String declaration = getDependencyString();
        try {
            Collection<MethodReference> all = new ArrayList<MethodReference>();
            Iterable<MethodLocator> locators = MethodLocator.parseAll(declaration);
            for (MethodLocator each: locators) all.add(each.resolve(getActualClass()));
            return all;
        } catch (InvalidDeclarationError ex) {
            return Collections.singleton(new MethodReference(ex));
        }
    }

}
