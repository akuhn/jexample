/**
 * 
 */
package jexample.internal;

import static jexample.internal.ExampleState.GREEN;
import static jexample.internal.ExampleState.RED;
import static jexample.internal.ExampleState.WHITE;

import java.lang.reflect.InvocationTargetException;

import org.junit.Ignore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * Invokes an example, caches the result and notifies JUnit.
 * 
 * @author Adrian Kuhn
 *
 */
class ExampleInvoker {

	private final Example $;
	private final RunNotifier notifier;

	public ExampleInvoker(Example example, RunNotifier notifier) {
		this.$ = example;
		this.notifier = notifier;
	}

	private boolean runDependencies() {
		for (Example provider : $.providers) {
			provider.run(notifier);
			if (provider.result != GREEN)
				return false;
		}
		return true;
	}

	private void executeExample() throws InvocationTargetException,
			Exception {
		Object[] args = $.providers.getInjectionValues($.policy, $.method.arity());
		Object container = $.getContainerInstance();
		Object result = $.method.invoke(container, args);
		if ($.expectedException == null) {
			$.returnValue.assign(result);
			$.returnValue.assignInstance(container);
		}
	}

	private ExampleState fail(Throwable e) {
		notifier.fireTestFailure(new Failure($.description, e));
		return RED;
	}

	private void finished() {
		notifier.fireTestFinished($.description);
	}

	private ExampleState ignore() {
		notifier.fireTestIgnored($.description);
		return WHITE;
	}

	private boolean isUnexpected(Throwable exception) {
		return !$.expectedException.isAssignableFrom(exception.getClass());
	}

	public ExampleState run() {
		$.owner.runBefores();
		if (!$.errors.isEmpty()) {
			started();
			fail($.errors);
			finished();
			return RED;
		}
		if (toBeIgnored())
			return ignore();
		if (!runDependencies())
			return ignore();
		started();
		try {
			return runExample();
		} finally {
			finished();
		}
	}

	private ExampleState runExample() {
		try {
			executeExample();
			if ($.expectedException != null) return failExpectedException();
			return success();
		} catch (InvocationTargetException e) {
			Throwable actual = e.getTargetException();
			if ($.expectedException == null) return fail(actual);
			if (isUnexpected(actual)) return failUnexpectedException(actual);
			return success();
		} catch (Throwable e) {
			return fail(e);
		}
	}

	private ExampleState failExpectedException() {
		return fail(new AssertionError("Expected exception: "
				+ $.expectedException.getName()));
	}
	
	private ExampleState failUnexpectedException(Throwable ex) {
		String message = "Unexpected exception, expected<"
			+ $.expectedException.getName() + "> but was<"
			+ ex.getClass().getName() + ">";
		return fail(new Exception(message, ex));
	}

	private void started() {
		notifier.fireTestStarted($.description);
	}

	private ExampleState success() {
		return GREEN;
	}
	
	private boolean toBeIgnored() {
		return $.method.getAnnotation(Ignore.class) != null;
	}

}