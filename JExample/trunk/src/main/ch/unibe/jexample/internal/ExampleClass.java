package ch.unibe.jexample.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.RunNotifier;

import ch.unibe.jexample.JExample;
import ch.unibe.jexample.internal.util.JExampleError;
import ch.unibe.jexample.internal.util.MethodReference;
import ch.unibe.jexample.internal.util.Reflection;
import ch.unibe.jexample.internal.util.JExampleError.Kind;

/**
 * Java class with example methods.
 * 
 * @author Lea Haensenberger
 * @author Adrian Kuhn
 * 
 */
public class ExampleClass {

    private boolean beforesRunned = false;
    private final JExampleError errors;
    private final ExampleGraph graph;

    private final Class<?> jclass;


    /*default*/ ExampleClass(Class<?> jclass, ExampleGraph graph) {
        this.errors = new JExampleError();
        this.graph = graph;
        this.jclass = jclass;
    }

    public Collection<MethodReference> collectTestMethods() {
        Collection<MethodReference> result = new ArrayList<MethodReference>();
        for (Method each: jclass.getMethods()) {
            MethodReference ref = new MethodReference(jclass, each);
            if (ref.isTestAnnotationPresent()) result.add(ref);
        }
        return result;
    }

    public boolean contains(Example m) {
        return m.method.getActualClass().equals(jclass);
    }

    public void filter(final Filter filter) {
        graph.filter(filter);
    }

    public Description getDescription() {
        Description description = Description.createSuiteDescription(jclass);
        for (Example each: graph.getExamples()) {
            if (this.contains(each)) description.addChild(each.getDescription());
        }
        return description;
    }

    public Class<?> getImplementingClass() {
        return jclass;
    }

    public void initializeExamples() {
        for (MethodReference m: collectTestMethods()) {
            graph.makeExample(m);
        }
    }

    public void run(RunNotifier notifier) {
        graph.run(this, notifier);
    }

    public void runBeforeClassBefores() {
        if (beforesRunned) return;
        for (Method m: jclass.getMethods()) {
            if (!m.isAnnotationPresent(BeforeClass.class)) continue;
            Reflection.invoke(m, null);
        }
        beforesRunned = true;
    }

    public void validate() throws JExampleError {
        RunWith run = (RunWith) jclass.getAnnotation(RunWith.class);
        if (run == null || run.value() != JExample.class) {
            errors.add(Kind.MISSING_RUNWITH_ANNOTATION,
                    "Class %s is not a JExample test class, annotation @RunWith(JExample.class) missing.", this);
        }
        try {
            Reflection.hasConstructorOrFail(jclass);
        } catch (NoSuchMethodException ex) {
            errors.add(Kind.MISSING_CONSTRUCTOR, ex);
        } catch (SecurityException ex) {
            errors.add(Kind.MISSING_CONSTRUCTOR, ex);
        }
        if (collectTestMethods().isEmpty()) {
            errors.add(Kind.NO_EXAMPLES_FOUND, "Test class must contain test methods.");
        }
        if (!errors.isEmpty()) throw errors;
    }

}
