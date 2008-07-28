package jexample;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.notification.RunNotifier;

import jexample.internal.*;

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
		TestGraph graph = createTestGraph(test);
		for (TestMethod each : graph.getTestMethods()) {
			// TODO use dependency parser to find matching method
			if (each.getDeclaringMethod().getName().equals(method)) {
				RunNotifier notifier = new RunNotifier();
				each.run(notifier);
				// TODO check notifier for errors/assertions
				return (T) each.getReturnValue();
			}
		}
		// TODO verbose error message
		throw new IllegalArgumentException("Method not found");
	}

	private static TestGraph createTestGraph(Class test) {
		try {
			TestGraph graph = new TestGraph();
			graph.add(test.getClass());
			return graph;
		} catch (InitializationError ex) {
			// TODO verbose error message
			throw new IllegalArgumentException(ex);
		}
	}
	
}
