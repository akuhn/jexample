package jexample;

import jexample.internal.Example;
import jexample.internal.ExampleGraph;

import org.junit.runners.model.InitializationError;
import org.junit.runner.notification.RunNotifier;

/**
 * Exercises test methods as sophisticated constructor to create example instances.
 * 
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
