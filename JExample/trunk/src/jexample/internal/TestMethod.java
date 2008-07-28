package jexample.internal;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jexample.Depends;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * The states, a {@link TestMethod} can have.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
enum TestResult {
	NOT_YET_RUN, GREEN, RED, WHITE
}

/**
 * The wrapper for the {@link Method}'s to be run.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class TestMethod {

	private Method javaMethod;
	private List<TestMethod> dependencies;
	private TestResult state;
	private Object returnValue;
    private Description description;
    private TestGraph graph;

    
	public TestMethod(Method m, TestGraph graph) {
	    assert graph != null;
        this.javaMethod = m;
        this.dependencies = new ArrayList<TestMethod>();
        this.state = TestResult.NOT_YET_RUN;
        this.description = Description.createTestDescription(m
                    .getDeclaringClass(), m.getName());
        this.graph = graph;
    }

	/**
	 * Checks, if this {@link TestMethod} belongs to <code>testClass</code>
	 * 
	 * @param testClass
	 *            the {@link TestClass} to be compared
	 * @return true, if the {@link TestMethod} belongs to <code>testClass</code>,
	 *         false otherwise
	 */
	public boolean belongsToClass(TestClass testClass) {
		return this.javaMethod.getDeclaringClass().equals(
				testClass.getJavaClass());
	}

	/**
	 * Runs this {@link TestMethod} after it run all of its dependencies.
	 * 
	 * @param notifier
	 *            the {@link RunNotifier}
	 */
	public void run(RunNotifier notifier) {
		if (this.hasBeenRun())
			return;
		boolean allParentsGreen = true;
		for (TestMethod dependency : this.dependencies) {
			dependency.run(notifier);
			allParentsGreen = allParentsGreen && dependency.isGreen();
		}
		if (allParentsGreen && !this.isIgnoredByAnnotation()) {
			this.runTestMethod(notifier);
		} else {
			this.setWhite();
			notifier.fireTestIgnored(this.getDescription());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
    public boolean equals(Object obj) {
		return this.javaMethod.equals(((TestMethod) obj).javaMethod);
	}

	/**
	 * If the TestMethod doesn't already have the dependency
	 * <code>testMethod</code>, <code>testMethod</code> is added as a
	 * dependency.
	 * 
	 * @param testMethod
	 *            the {@link TestMethod} to be added as a dependency
	 */
	public void addDependency(TestMethod testMethod) {
		if (!this.dependencies.contains(testMethod)) {
			this.dependencies.add(testMethod);
		}
	}

	/**
	 * @return a {@link List} of {@link TestMethod}'s, being the dependencies
	 */
	public List<TestMethod> getDependencies() {
		return this.dependencies;
	}

	/**
	 * @return the test {@link Description} of this {@link TestMethod}
	 */
	public Description getDescription() {
		return description;
	}

	/**
	 * @return the declaring {@link Class} of <code>javaMethod</code>
	 */
	public Class<?> getDeclaringClass() {
		return this.javaMethod.getDeclaringClass();
	}

	private void runTestMethod(RunNotifier notifier) {
		Object test;
		try {
			Constructor<?> constructor = this.javaMethod.getDeclaringClass().getDeclaredConstructor();
			constructor.setAccessible(true);
			test = constructor.newInstance();
			this.invokeMethod(test, notifier, this.getArguments());
		} catch (InvocationTargetException e) {
			notifier.testAborted(description, e.getCause());
			return;
		} catch (Exception e) {
			notifier.testAborted(description, e);
			return;
		}
	}

	private void reRunTestMethod() throws Exception {
	    Constructor<?> constructor = this.javaMethod.getDeclaringClass().getConstructor();
	    constructor.setAccessible(true);
		Object test = constructor.newInstance();
		this.returnValue = this.javaMethod.invoke(test, this.getArguments());
	}

	/**
	 * Collects all the arguments taken by the test method. If
	 * <code>clone</code> is implemented, the arguments are cloned.
	 * 
	 * @param notifier
	 * 
	 * @return an {@link Array} of arguments to be passed to the test method
	 *         when invoking it.
	 * @throws Exception if the return value could not be cloned and the provider method could
	 * not be re-run
	 */
	private Object[] getArguments() throws Exception {
		Class<?>[] paramTypes = this.javaMethod.getParameterTypes();
		Object[] arguments = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			if (this.dependencies.get(i).returnValue != null) {
				if (this.typeIsCloneable(paramTypes[i])) {
					arguments[i] = this.cloneReturnValue(this.dependencies
							.get(i).returnValue, paramTypes[i]);
				} else {
					// TODO: run test method again, so you get a new instance of
					// the return value
					TestMethod provider = this.dependencies.get(i);
					provider.reRunTestMethod();
					arguments[i] = provider.returnValue;
				}
			}
		}

		return arguments;
	}

	// really ugly method, but java leaves no alternative, i think
	private Object cloneReturnValue(Object returnValue, Class<?> clazz) {
		Object cloned = null;
		try {
			Method cloneMethod = clazz.getMethod("clone");
			cloneMethod.setAccessible(true);
			cloned = cloneMethod.invoke(returnValue);
		} catch (Exception e) {
			return returnValue;
		}
		return cloned;
	}

	/**
	 * Checks if <code>clazz</code> or one of its superlcasses implements
	 * {@link Cloneable} and declares a {@link Method} <code>clone()</code>.
	 * 
	 * @param clazz
	 *            the {@link Class} to check, if it is cloneable
	 * @return true, if all this conditions are fulfilled, false otherwise
	 */
	private boolean typeIsCloneable(Class<?> clazz) {
		for (Class<?> iface : clazz.getInterfaces()) {
			if (iface.equals(Cloneable.class)) {
				try {
					clazz.getMethod("clone");
				} catch (Exception e) {
					return false;
				}
				return true;
			}
		}
		if (clazz.getSuperclass() != null) {
			return this.typeIsCloneable(clazz.getSuperclass());
		} else {
			return false;
		}
	}

	private void invokeMethod(Object test, RunNotifier notifier,
			Object... args) {
		notifier.fireTestStarted(description);
		try {
		    this.javaMethod.setAccessible(true);
			this.returnValue = this.javaMethod.invoke(test, args);
			this.setGreen();
		} catch (InvocationTargetException e) {
			Throwable actual = e.getTargetException();
			if (!this.expectsException()) {
				this.addFailure(actual, notifier);
			} else if (this.isUnexpectedException(actual)) {
				String message = "Unexpected exception, expected<"
						+ this.getExpectedException().getName() + "> but was<"
						+ actual.getClass().getName() + ">";
				this.addFailure(new Exception(message, actual), notifier);
			}
		} catch (Throwable e) {
			this.addFailure(e, notifier);
		} finally {
			notifier.fireTestFinished(description);
		}
	}

	private boolean isUnexpectedException(Throwable actual) {
		return this.getExpectedException() != actual.getClass();
	}

	private boolean expectsException() {
		return this.getExpectedException() != null;
	}

	private Class<? extends Throwable> getExpectedException() {
		Test annotation = this.javaMethod.getAnnotation(Test.class);
		if (annotation != null
				&& annotation.expected() != org.junit.Test.None.class) {
			return annotation.expected();
		}
		return null;
	}

	private void addFailure(Throwable e, RunNotifier notifier) {
		notifier.fireTestFailure(new Failure(description, e));
		this.setFailed();
	}

	private boolean isIgnoredByAnnotation() {
		return this.javaMethod.getAnnotation(Ignore.class) != null;
	}

	private void setGreen() {
		this.state = TestResult.GREEN;
	}

	private void setWhite() {
		this.state = TestResult.WHITE;
	}

	private void setFailed() {
		this.state = TestResult.RED;
	}

	private boolean isGreen() {
		return state == TestResult.GREEN;
	}

	private boolean hasBeenRun() {
		return state != TestResult.NOT_YET_RUN;
	}

	public Method getDeclaringMethod() {
		return this.getJavaMethod();
	}

	public Object getReturnValue() {
		return this.returnValue;
	}

    public Method getJavaMethod() {
        return javaMethod;
    }

    public Collection<Method> dependencies() {
        DependencyParser parser = new DependencyParser( javaMethod.getDeclaringClass() );
        List<Method> deps = new ArrayList<Method>();
        Depends annotation = javaMethod.getAnnotation( Depends.class );
        if ( annotation != null ) {
            try {
                deps = parser.getDependencies( ( ( Depends ) annotation ).value() );
            } catch (SecurityException ex) {
                graph.addInitializationError(ex);
            } catch (ClassNotFoundException ex) {
                graph.addInitializationError(ex);
           } catch (NoSuchMethodException ex) {
               graph.addInitializationError(ex);
            }
        }
        return deps;
    }

    public Object getName() {
        return javaMethod.getName();
    }

    public void validate() throws InitializationError {
        if (!javaMethod.isAnnotationPresent(Test.class)) {
            Exception ex = new Exception();
            ex.fillInStackTrace();
            graph.addInitializationError(ex);
        }
    }

}
