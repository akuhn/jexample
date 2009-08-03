/**
 * 
 */
package ch.unibe.jexample.internal;

import static ch.unibe.jexample.internal.ExampleColor.GREEN;
import static ch.unibe.jexample.internal.ExampleColor.RED;
import static ch.unibe.jexample.internal.ExampleColor.WHITE;

import java.lang.reflect.InvocationTargetException;

import org.junit.Ignore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

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

    private ExampleColor fail(Throwable e) {
        notifier.fireTestFailure(new Failure(example.getDescription(), e));
        return RED;
    }

    private ExampleColor failExpectedException() {
        return fail(new AssertionError("Expected exception: " + example.expectedException.getName()));
    }

    private ExampleColor failUnexpectedException(Throwable ex) {
        String message = "Unexpected exception, expected<" + example.expectedException.getName() + "> but was<"
                + ex.getClass().getName() + ">";
        return fail(new Exception(message, ex));
    }

    private void finished() {
        notifier.fireTestFinished(example.getDescription());
    }

    private ExampleColor ignore() {
        notifier.fireTestIgnored(example.getDescription());
        return WHITE;
    }

    private boolean isUnexpected(Throwable exception) {
        return !example.expectedException.isAssignableFrom(exception.getClass());
    }

    /**
     * Runs the example and returns test color.
     * 
     * @return {@link GREEN} if the example was successful,<br> {@link RED} if the
     *         example is invalid or failed, and<br> {@link WHITE} if any of the
     *         dependencies failed.
     */
    public ExampleColor run() {
        if (example.errors.size() > 0) return abort(example.errors);
        if (toBeIgnored()) return ignore();
        if (!runDependencies()) return ignore();
        started();
        try {
            return runExample();
        } finally {
            finished();
        }
    }

    private ExampleColor abort(Throwable ex) {
        notifier.fireTestStarted(example.getDescription());
        notifier.fireTestFailure(new Failure(example.getDescription(), ex));
        notifier.fireTestFinished(example.getDescription());
        return RED;
    }

    /**
     * Runs dependencies and returns success. Dependencies are ran in order
     * of declaration.
     * 
     * @return If all dependencies succeed return <code>true</code>.<br>
     *         If any fails, abort and return <code>false</code>.
     */
    private boolean runDependencies() {
        for (Dependency each: example.producers()) {
            Example eg = each.dependency();
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
    private ExampleColor runExample() {
        try {
            example.bareInvoke();
            if (example.expectedException != null) return failExpectedException();
            return success();
        } catch (InvocationTargetException e) {
            Throwable actual = e.getTargetException();
            if (example.expectedException == null) return fail(actual);
            if (isUnexpected(actual)) return failUnexpectedException(actual);
            return success();
        } catch (Throwable e) {
            return fail(e);
        }
    }

    private void started() {
        notifier.fireTestStarted(example.getDescription());
    }

    private ExampleColor success() {
        return GREEN;
    }

    private boolean toBeIgnored() {
        return example.method.getAnnotation(Ignore.class) != null;
    }

}