package jexample;

import jexample.internal.ExampleGraph;
import jexample.internal.JExampleError;
import jexample.internal.ExampleClass;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;


/**
 * Runs JExample tests. Delegates all logic to the singleton {@link ExampleGraph}. Assumes
 * that clients first create an instance for each classes under test, and only when all
 * instances are created start calling {@link #run(RunNotifier)} on any of these instances. Current versions
 * of JUnit's eclipse plug-in do so (as of Eclipse 3.4 and JUnit 4.4).
 *  
 * @author Lea Haensenberger 
 * @author Adrian Kuhn
 * 
 */
public class JExample extends Runner implements Filterable {

	private final ExampleClass testCase;
	
	public JExample(Class<?> testClass) throws JExampleError {
		this.testCase = ExampleGraph.instance().add(testClass);
	}
	
	public JExample(ExampleClass testCase) {
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

    public void filter(Filter filter) throws NoTestsRemainException {
        testCase.filter(filter);
    }
 
}
