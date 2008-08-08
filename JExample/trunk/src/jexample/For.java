package jexample;

import jexample.internal.Example;
import jexample.internal.ExampleGraph;
import jexample.internal.TestClass;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.notification.RunNotifier;

/**
 * Exercises test methods as sophisticated constructor to create example instances.
 * 
 *
 */

public class For {

	private For() {
		throw new IllegalAccessError();
	}
	
	public static <T> T example(Class jClass, String method) {
	    Example e = findExample(jClass, method);
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
            TestClass test = graph.add(jClass);
            for (Example e : graph.getExamples()) {
                if (test.contains(e) && e.jmethod.getName().equals(method)) return e;
            }
        } catch(InitializationError ex) { throw new RuntimeException(ex); };    
        throw new IllegalArgumentException("Method not found");
    }

}
