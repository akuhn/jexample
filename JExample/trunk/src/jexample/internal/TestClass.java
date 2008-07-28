package jexample.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jexample.Depends;

import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;


/**
 * A wrapper for the {@link Class} under test.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class TestClass {
    
    private final TestGraph graph;
	private final Class<?> fClass;

	public TestClass(Class<?> fClass, TestGraph graph) {
		this.fClass = fClass;
		this.graph = graph;
	}

	/**
	 * @return a {@link List} of all {@link Method}'s annotated with {@link Test}
	 */
	public List<Method> getTestMethods() {
		return getAnnotatedMethods( Test.class );
	}

	/**
	 * @return the {@link Constructor} of <code>fClass</code>
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public Constructor<?> getConstructor() throws SecurityException, NoSuchMethodException {
	    Constructor<?> $ = fClass.getDeclaredConstructor();
	    $.setAccessible(true);
	    return $;
	}

	/**
	 * @return the {@link Class} object of <code>fClass</code>
	 */
	public Class<?> getJavaClass() {
		return fClass;
	}

	/**
	 * @return the name of <code>fClass</code>
	 */
	public String getName() {
		return fClass.getName();
	}

	private List<Method> getAnnotatedMethods(Class<? extends Annotation> annotationClass) {
		List<Method> $ = new ArrayList<Method>();
		for (Method m : fClass.getMethods()) {
		    if (m.isAnnotationPresent(annotationClass)) {
		        $.add(m);
		    }
		}
		return $;
	}

    public Description getDescription() {
        return graph.descriptionForClass(this);
    }

    public void run(RunNotifier notifier) {
        graph.run(this, notifier);
    }

    public TestClass validate() throws InitializationError {
        try {
            this.getConstructor();
        } catch (Exception ex) {
            throw new InitializationError(ex);
        }
        return this;
    }

}
