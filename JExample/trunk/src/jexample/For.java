package jexample;

import jexample.internal.Example;
import jexample.internal.ExampleGraph;
import jexample.internal.TestClass;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Result;
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
	    try {
	        ExampleGraph graph = new ExampleGraph();
	        TestClass test = graph.add(jClass);
	        for (Example e : graph.getExamples()) {
	            if (test.contains(e) && e.jmethod.getName().equals(method)) {
	                return (T) runExample(e);
			    }
		    }
	    } catch(InitializationError ex) { throw new RuntimeException(ex); };    
	    throw new IllegalArgumentException("Method not found");
	}

    private static Object runExample(Example e) {
        Result r = new Result();
        RunNotifier n = new RunNotifier();
        n.addListener(r.createListener());
        e.run(n);
        // TODO should check if e was succesful. If no test failed is not the same.
        if (!r.wasSuccessful()) throw new RuntimeException("Test failed.");
        return e.returnValue;
    }

}
