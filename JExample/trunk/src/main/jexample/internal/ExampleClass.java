package jexample.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import jexample.JExample;
import jexample.internal.JExampleError.Kind;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.RunNotifier;


/**
 * Java class with example methods.
 * 
 * @author Lea Haensenberger
 * @author Adrian Kuhn
 * 
 */
public class ExampleClass {
    
    public final Class jclass;

    private final JExampleError errors;    
    private final ExampleGraph graph;
    private boolean beforesRunned = false;
	
	
    public ExampleClass(Class<?> jclass, ExampleGraph graph) {
        this.errors = new JExampleError();
        this.graph = graph;
		this.jclass = jclass;
	}


    public List<MethodReference> collectTestMethods() {
		List<MethodReference> $ = new ArrayList<MethodReference>();
        for (MethodReference m : MethodReference.all(jclass)) {
            if (m.isAnnotationPresent(Test.class)) {
                $.add(m);
            }
        }
        return $;
	}
	

	public Description getDescription() {
        Description $ = Description.createSuiteDescription(jclass);
        for (Example tm : graph.getExamples())
            if (this.contains(tm))
                $.addChild(tm.description);
        return $;
    }    

    public void run(RunNotifier notifier) {
        graph.run(this, notifier);
    }
    
    public static Constructor getConstructor(Class jClass) throws SecurityException, NoSuchMethodException {
        if (!Modifier.isPublic(jClass.getModifiers())) {
            Constructor $ = jClass.getDeclaredConstructor();
            $.setAccessible(true);
            return $;
        }
        return jClass.getConstructor();
    }

    public void validate() throws JExampleError {
        RunWith run = (RunWith) jclass.getAnnotation(RunWith.class);
        if (run == null || run.value() != JExample.class ) {
            errors.add(Kind.MISSING_RUNWITH_ANNOTATION,
                    "Class %s is not a JExample test class, annotation @RunWith(JExampleRunner.class) missing.", this);
        }
        try {
            getConstructor(jclass);
        }
        catch (NoSuchMethodException ex) {
            errors.add(Kind.MISSING_CONSTRUCTOR, ex);
        }
        catch (SecurityException ex) {
            errors.add(Kind.MISSING_CONSTRUCTOR, ex);
        }
        if (collectTestMethods().isEmpty()) {
            errors.add(Kind.NO_EXAMPLES_FOUND, "Test class must contain test methods.");
        }
        if (!errors.isEmpty()) throw errors;
     }

     public void filter(final Filter filter) {
        graph.filter(filter);
    }
    
    public boolean contains(Example m) {
       return m.method.jclass.equals(jclass);
    }

    public void initializeExamples() {
        for (MethodReference m : collectTestMethods()) {
            graph.newExample(m);
        }
    }
    
    public void runBefores() {
        if (beforesRunned) return;
        for (Method m : jclass.getMethods()) {
            if (m.isAnnotationPresent(BeforeClass.class)) {
                try {
                    m.invoke(null);
                } catch (IllegalArgumentException ex) {
                    throw new RuntimeException(ex);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                } catch (InvocationTargetException ex) {
              }
            }
        }
        beforesRunned = true;
    }

}
