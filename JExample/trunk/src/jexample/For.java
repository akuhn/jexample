package jexample;

import jexample.internal.Example;
import jexample.internal.ExampleGraph;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.notification.RunNotifier;

/**
 * Exercises test methods as sophisticated constructor to create example instances.
 * 
 *
 */

public class For {

	private For() {
		// TODO make non-static class
	}
	
	public static <T> T example(Class test, String method) {
		ExampleGraph graph = createTestGraph(test);
		for (Example each : graph.getExamples()) {
			// TODO use dependency parser to find matching method
			if (each.jmethod.getName().equals(method)) {
				RunNotifier notifier = new RunNotifier();
				each.run(notifier);
				// TODO check notifier for errors/assertions
				return (T) each.returnValue;
			}
		}
		// TODO verbose error message
		throw new IllegalArgumentException("Method not found");
	}

	private static ExampleGraph createTestGraph(Class test) {
		try {
			ExampleGraph graph = new ExampleGraph();
			graph.add(test.getClass());
			return graph;
		} catch (InitializationError ex) {
			// TODO verbose error message
			throw new IllegalArgumentException(ex);
		}
	}
	
}
