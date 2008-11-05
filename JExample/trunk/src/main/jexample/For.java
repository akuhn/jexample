package jexample;

import jexample.internal.Example;
import jexample.internal.ExampleGraph;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.notification.RunNotifier;

/**
 * Creates example instances. Sometimes, when you are writing a script or
 * hacking on spike-prototype you might need examples instances of objects.
 * You can use this class to create them:
 * 
 * <pre>
 *     Stack stack = For.example(Stack.class, "withManyValues");
 * </pre>
 * 
 * @author Adrian Kuhn, 2007-2008
 * 
 */
public abstract class For {

	public static <T> T example(Class jClass, String method) {
	    Example e = findExample(jClass, method);
	    if (e == null) throw new NoSuchMethodError("Method not found.");
	    return (T) runExample(e);
	}

    private static Object runExample(Example e) {
        e.run(new RunNotifier());
        if (!e.wasSuccessful()) throw new RuntimeException("Test failed.");
        return e.returnValue.getValue();
    }
    
    private static Example findExample(Class jClass, String method) {
        try {
            ExampleGraph graph = new ExampleGraph();
            graph.add(jClass);
            return graph.findExample(jClass, method);
        } catch(InitializationError ex) { throw new RuntimeException(ex); }    
    }

}
