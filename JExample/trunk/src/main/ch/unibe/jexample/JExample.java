package ch.unibe.jexample;

import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;

import ch.unibe.jexample.internal.ExampleClass;
import ch.unibe.jexample.internal.ExampleGraph;
import ch.unibe.jexample.internal.util.JExampleError;

/**
 * Runs JExample tests. Delegates all logic to the singleton
 * {@link ExampleGraph}. Assumes that JUnit first creates an instance of each
 * classes under test, and only when all instances are created starts calling
 * {@link #run(RunNotifier)} on any of these instances. Current versions of
 * JUnit's eclipse plug-in do so (as of Eclipse 3.4 and JUnit 4.4).
 * <p>
 * All test classes passed to this Runner must be annotated with &#64;
 * {@link RunWith}(JExample.class) annotations.
 * 
 * @author Adrian Kuhn, 2007-2008
 * @author Lea Haensenberger, 2007
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

    public void filter(Filter filter) throws NoTestsRemainException {
        testCase.filter(filter);
    }

    @Override
    public Description getDescription() {
        return testCase.getDescription();
    }

    @Override
    public void run(RunNotifier notifier) {
        testCase.run(notifier);
    }

}
