package jexample.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jexample.JExampleRunner;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;


/**
 * Java class with test methods.
 * 
 * @author Lea Haensenberger
 * @author Adrian Kuhn
 * 
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
	public List<Method> collectTestMethods() {
		List<Method> $ = new ArrayList<Method>();
        for (Method m : fClass.getMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                $.add(m);
            }
        }
        return $;
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

    public Description getDescription() {
        return graph.descriptionForClass(this);
    }

    public void run(RunNotifier notifier) {
        graph.run(this, notifier);
    }

    public TestClass validate() {
        RunWith run = fClass.getAnnotation(RunWith.class);
        if (run == null || run.value() != JExampleRunner.class ) {
            graph.throwNewError("Class %s is not a JExample test class, annotation @RunWith(JExampleRunner.class) missing.", this);
        }
        try {
            fClass.getConstructor();
        }
        catch (NoSuchMethodException ex) {
            graph.addInitializationError(ex);
        }
        catch (SecurityException ex) {
            graph.addInitializationError(ex);
        }
        int len = collectTestMethods().size();
        if (len == 0) {
            graph.throwNewError("Class %s does not contain test methods.", this);
        }
        return this;
    }

}
