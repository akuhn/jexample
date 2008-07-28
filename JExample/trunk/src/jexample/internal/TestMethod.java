package jexample.internal;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jexample.Depends;
import jexample.InjectionPolicy;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * The wrapper for the {@link Method}'s to be run.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 * @author Adrian Kuhn
 * 
 */
public class TestMethod {

	private List<TestMethod> dependencies;
	private Description description;
	private TestGraph graph;
	private Method javaMethod;
    private TestResult result;
    private Object returnValue;

    
	public TestMethod(Method m, TestGraph graph) {
	    assert graph != null;
        this.javaMethod = m;
        this.dependencies = new ArrayList<TestMethod>();
        this.result = TestResult.NOT_YET_RUN;
        this.description = Description.createTestDescription(m
                    .getDeclaringClass(), m.getName());
        this.graph = graph;
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
		this.dependencies.add(testMethod);
		// TODO duplicate dependencies? an error or not? I'd say no.
	}

	private void addFailure(Throwable e, RunNotifier notifier) {
		notifier.fireTestFailure(new Failure(description, e));
		this.setFailed();
	}

	public boolean belongsToClass(TestClass testClass) {
		return this.javaMethod.getDeclaringClass().equals(
				testClass.getJavaClass());
	}

	private Object cloneReturnValue() {
		try {
            Method cloneMethod = returnValue.getClass().getMethod("clone");
            return cloneMethod.invoke(returnValue);
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
	}

	public Collection<Method> collectDependencies() {
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

	private boolean expectsException() {
		return this.getExpectedException() != null;
	}

	private Object[] getInjectionValues() throws Exception {
        Object[] $ = new Object[javaMethod.getParameterTypes().length];
		for (int i = 0; i < $.length; i++) {
			$[i] = getInjectionValue(dependencies.get(i));
		}
		return $;
	}

	/**
	 * @return the declaring {@link Class} of <code>javaMethod</code>
	 */
	public Class<?> getDeclaringClass() {
		return this.javaMethod.getDeclaringClass();
	}

	public Method getDeclaringMethod() {
		return this.getJavaMethod();
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

    private Class<? extends Throwable> getExpectedException() {
		Test a = this.javaMethod.getAnnotation(Test.class);
		if (a == null) return null;
		if (a.expected() == org.junit.Test.None.class) return null;
		return a.expected();
	}

	private Object getInjectionValue(TestMethod testMethod) throws Exception {
        if (testMethod.returnValue == null) return null;
        if (testMethod.isCloneable()) return testMethod.cloneReturnValue();
        if (keepReturnValue()) return testMethod.getReturnValue();
        TestMethod provider = testMethod;
	    provider.reRunTestMethod();
	    return provider.returnValue;
    }

	public Method getJavaMethod() {
        return javaMethod;
    }

	public Object getName() {
        return javaMethod.getName();
    }

	public Object getReturnValue() {
		return this.returnValue;
	}

	private boolean hasBeenRun() {
		return result != TestResult.NOT_YET_RUN;
	}

	private void invokeMethod(Object test, RunNotifier notifier, Object... args) {
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

	private boolean isGreen() {
		return result == TestResult.GREEN;
	}

	private boolean isIgnoredByAnnotation() {
		return this.javaMethod.getAnnotation(Ignore.class) != null;
	}

	private boolean isUnexpectedException(Throwable actual) {
		return this.getExpectedException() != actual.getClass();
	}

	private boolean keepReturnValue() {
        InjectionPolicy policy = getDeclaringClass().getAnnotation(InjectionPolicy.class);
        return policy != null && policy.keep();
    }

	private void reRunTestMethod() throws Exception {
	    Constructor<?> constructor = this.javaMethod.getDeclaringClass().getConstructor();
	    constructor.setAccessible(true);
		Object test = constructor.newInstance();
		this.returnValue = this.javaMethod.invoke(test, this.getInjectionValues());
	}

	/**
	 * Runs this {@link TestMethod} after it run all of its dependencies.
	 * 
	 * @param notifier
	 *            the {@link RunNotifier}
	 */
	public void run(RunNotifier notifier) {
		if (this.hasBeenRun()) return;
		boolean allParentsGreen = true;
		for (TestMethod dependency : this.dependencies) {
			dependency.run(notifier);
			allParentsGreen &= dependency.isGreen();
		}
		if (allParentsGreen && !this.isIgnoredByAnnotation()) {
			this.runTestMethod(notifier);
		} else {
			this.setWhite();
			notifier.fireTestIgnored(this.getDescription());
		}
	}

	private void runTestMethod(RunNotifier notifier) {
		try {
			this.invokeMethod(newTestCase(), notifier, this.getInjectionValues());
		} catch (InvocationTargetException e) {
			notifier.testAborted(description, e.getCause());
		} catch (Exception e) {
			notifier.testAborted(description, e);
		}
	}

    private Object newTestCase() throws NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Object test;
        Constructor<?> constructor = this.javaMethod.getDeclaringClass().getDeclaredConstructor();
        constructor.setAccessible(true);
        test = constructor.newInstance();
        return test;
    }

	private void setFailed() {
		this.result = TestResult.RED;
	}

    private void setGreen() {
		this.result = TestResult.GREEN;
	}

    private void setWhite() {
		this.result = TestResult.WHITE;
	}

    /**
     * Checks if <code>clazz</code> or one of its superlcasses implements
     * {@link Cloneable} and declares a {@link Method} <code>clone()</code>.
     * 
     * @param clazz
     *            the {@link Class} to check, if it is cloneable
     * @return true, if all this conditions are fulfilled, false otherwise
     */
    private boolean isCloneable() {
        if (returnValue == null) return true;
        if (!(returnValue instanceof Cloneable)) return false;
        try {
            returnValue.getClass().getMethod("clone");
        } catch (SecurityException ex) {
            return false;
        } catch (NoSuchMethodException ex) {
            return false;
        }
        return true;
    }

    public void validate() {
        if (!javaMethod.isAnnotationPresent(Test.class)) {
            graph.throwNewError("Method %s is not a test method, missing @Test annotation.", getName());
        }
        int d = getDependencies().size();
        int p = javaMethod.getParameterTypes().length;
        if (p > 0 && p != d) {
            graph.throwNewError("Method %s has %d parameters but %d dependencies.", getName(), p, d);
        }
        else {
            validateDependencyTypes();
        }
    }

    private void validateDependencyTypes() {
        Iterator<TestMethod> tms = getDependencies().iterator();
        for (Class<?> t : javaMethod.getParameterTypes()) {
            TestMethod tm = tms.next();
            Class<?> r = tm.getJavaMethod().getReturnType();
            if (!t.isAssignableFrom(r)) {
                graph.throwNewError("Parameter %s is not assignable from depedency %s.", t, tm);
            }
        }
    }
    

}

/**
 * The states, a {@link TestMethod} can have.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
enum TestResult {
	GREEN, NOT_YET_RUN, RED, WHITE
}
