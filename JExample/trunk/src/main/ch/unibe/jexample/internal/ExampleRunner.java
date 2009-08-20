/**
 * 
 */
package ch.unibe.jexample.internal;

import java.lang.reflect.InvocationTargetException;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import ch.unibe.jexample.internal.graph.Edge;

/**
 * Runs an example, reports to JUnit and returns test color.
 * 
 * @author Adrian Kuhn
 * 
 */
class ExampleRunner {

    private final Example example;
    private final RunNotifier notifier;

    public ExampleRunner(Example example, RunNotifier notifier) {
        this.example = example;
        this.notifier = notifier;
    }

    /**
     * Runs the example and returns test color.
     * 
     * @return {@link GREEN} if the example was successful,<br> {@link RED} if the
     *         example is invalid or failed, and<br> {@link WHITE} if any of the
     *         dependencies failed.
     */
    public ReturnValue run() {
        if (example.hasErrors()) return abort(example.errors);
        if (example.method.isIgnorePresent()) return ignore();
        if (!runDependencies()) return ignore();
        started();
        try {
            return runExample();
        } finally {
            finished();
        }
    }

    private ReturnValue abort(Throwable ex) {
        notifier.fireTestStarted(example.getDescription());
        notifier.fireTestFailure(new Failure(example.getDescription(), ex));
        notifier.fireTestFinished(example.getDescription());
        return ReturnValue.R_RED;
    }

    private ReturnValue fail(Throwable e) {
        notifier.fireTestFailure(new Failure(example.getDescription(), e));
        return ReturnValue.R_RED;
    }

    private ReturnValue failExpectedException() {
        return fail(new AssertionError("Expected exception: " + example.expectedException.getName()));
    }

    private ReturnValue failUnexpectedException(Throwable ex) {
        String message = "Unexpected exception, expected<" + example.expectedException.getName() + "> but was<"
                + ex.getClass().getName() + ">";
        return fail(new Exception(message, ex));
    }

    private void finished() {
        notifier.fireTestFinished(example.getDescription());
    }

    private ReturnValue ignore() {
        notifier.fireTestIgnored(example.getDescription());
        return ReturnValue.R_WHITE;
    }

    private boolean isUnexpected(Throwable exception) {
        return !example.expectedException.isAssignableFrom(exception.getClass());
    }

    /**
     * Runs dependencies and returns success. Dependencies are ran in order
     * of declaration.
     * 
     * @return If all dependencies succeed return <code>true</code>.<br>
     *         If any fails, abort and return <code>false</code>.
     */
    private boolean runDependencies() {
        for (Edge<Example> each: example.node.dependencies()) {
            Example eg = each.getProducer().value;
            eg.run(notifier);
            if (!eg.wasSuccessful()) return false;
        }
        return true;
    }

    /**
     * Runs the bare example and handles exceptions.
     * 
     * @return {@link GREEN} if the example runs without exception (or throws an
     *         expected exception).<br> {@link RED} if the example fails (or if an
     *         exception was expected, does not throw the expected exception).
     */
    private ReturnValue runExample() {
        try {
            ReturnValue value = example.bareInvoke();
            if (example.expectedException != null) return failExpectedException();
            return value;
        } catch (InvocationTargetException e) {
            Throwable actual = e.getTargetException();
            if (example.expectedException == null) return fail(actual);
            if (isUnexpected(actual)) return failUnexpectedException(actual);
            return ReturnValue.R_GREEN;
        } catch (Throwable e) {
            return fail(e);
        }
    }

    private void started() {
        notifier.fireTestStarted(example.getDescription());
    }

}