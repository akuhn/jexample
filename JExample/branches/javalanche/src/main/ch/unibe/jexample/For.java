package ch.unibe.jexample;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;

import ch.unibe.jexample.internal.Example;
import ch.unibe.jexample.internal.ExampleGraph;
import ch.unibe.jexample.internal.util.JExampleError;
import ch.unibe.jexample.internal.util.MethodLocator;
import ch.unibe.jexample.internal.util.MethodReference;

/**
 * Creates example instances. Sometimes, when you are writing a script or
 * hacking on spike-prototype you might need examples instances of objects. You
 * can use this class to create them:
 * 
 * <pre>
 * Stack stack = For.example(Stack.class, &quot;withManyValues&quot;);
 * </pre>
 * 
 * @author Adrian Kuhn, 2007-2008
 * 
 */
@SuppressWarnings("unchecked")
public abstract class For {

    public static <T> T example(Class jClass, String method) {
        Example e = findExample(jClass, method);
        if (e == null) throw new NoSuchMethodError("Method not found.");
        return (T) runExample(e);
    }

    public static <T> T example(String fullReference) {
        try {
            MethodReference ref = MethodLocator.parse(fullReference).resolve();
            ExampleGraph g = new ExampleGraph();
            g.add(ref.getActualClass());
            Example e = g.findExample(ref);
            return (T) runExample(e);
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        } catch (JExampleError ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Example findExample(Class jClass, String method) {
        try {
            ExampleGraph graph = new ExampleGraph();
            graph.add(jClass);
            return graph.findExample(jClass, method);
        } catch (InitializationError ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Object runExample(Example e) {
    	Result result= new Result();
		RunNotifier notifier = new RunNotifier();
		notifier.addFirstListener(result.createListener());
        e.run(notifier);
        if (!e.wasSuccessful()) {
        	if (result.getFailures().size() != 1) throw new RuntimeException("Oops, what do we now!?");
        	throw new RuntimeException(result.getFailures().iterator().next().getException());
        }
        return e.getReturnValue().getValue();
    }

}
