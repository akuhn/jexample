package jexample;

import jexample.internal.TestClass;
import jexample.internal.TestGraph;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;


/**
 * Runs JExample tests. Delegates all logic to the singleton {@link TestGraph}. Assumes
 * that clients first create an instance for each classes under test, and only when all
 * instances are created start calling {@link #run(RunNotifier)} on any of these instances. Current versions
 * of JUnit's eclipse plug-in do so (as of Eclipse 3.4 and JUnit 4.4).
 *  
 * @author Lea Haensenberger 
 * @author Adrian Kuhn
 * 
 */
public class JExampleRunner extends Runner {

	private final TestClass testCase;
	
	public JExampleRunner(Class<?> testClass) throws InitializationError {
		this.testCase = TestGraph.instance().add(testClass);
	}
	
	public JExampleRunner(TestClass testCase) {
	    this.testCase = testCase;
	}
	
	@Override
	public Description getDescription() {
	    return testCase.getDescription();
	}

	@Override
	public void run( RunNotifier notifier ) {
	    testCase.run(notifier);
	}

}
