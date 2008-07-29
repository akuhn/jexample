package jexample.internal;

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
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * Test method.
 * 
 * @author Lea Haensenberger
 * @author Adrian Kuhn
 * 
 */
public class TestMethod {

    public final Description description;
    public final ReturnValue returnValue;
    public final Method javaMethod;
    public final Dependencies providers;
    
	private final TestGraph context;
    private TestResult result;
    private InjectionPolicy policy;

    
	public TestMethod(Method javaMethod, TestGraph graph) {
	    assert javaMethod != null && graph != null;
        this.javaMethod = javaMethod;
        this.providers = new Dependencies();
        this.result = TestResult.NOT_YET_RUN;
        this.description = Description.createTestDescription(javaMethod.getDeclaringClass(), javaMethod.getName());
        this.context = graph;
        this.returnValue = new ReturnValue(this);
        this.policy = javaMethod.getClass().getAnnotation(InjectionPolicy.class);
    }


	public Collection<Method> collectDependencies() {
        DependencyParser parser = new DependencyParser(javaMethod.getDeclaringClass());
        List<Method> deps = new ArrayList<Method>();
        Depends annotation = javaMethod.getAnnotation( Depends.class );
        if ( annotation != null ) {
            try {
                deps = parser.getDependencies( ( ( Depends ) annotation ).value() );
            } catch (SecurityException ex) {
                context.addInitializationError(ex);
            } catch (ClassNotFoundException ex) {
                context.addInitializationError(ex);
           } catch (NoSuchMethodException ex) {
               context.addInitializationError(ex);
            }
        }
        return deps;
    }

	private boolean expectsException() {
		return this.getExpectedException() != null;
	}


	/**
	 * @return a {@link List} of {@link TestMethod}'s, being the dependencies
	 */
	public Dependencies getDependencies() {
		return this.providers;
	}
	

    private Class<? extends Throwable> getExpectedException() {
		Test a = this.javaMethod.getAnnotation(Test.class);
		if (a == null) return null;
		if (a.expected() == org.junit.Test.None.class) return null;
		return a.expected();
	}


	public Method getJavaMethod() {
        return javaMethod;
    }

	private boolean hasBeenRun() {
		return result != TestResult.NOT_YET_RUN;
	}

	private void invokeMethod(Object test, RunNotifier notifier, Object... args) {
		notifier.fireTestStarted(description);
		try {
		    this.javaMethod.setAccessible(true);
			returnValue.assign(this.javaMethod.invoke(test, args));
			this.result = TestResult.GREEN;
		} catch (InvocationTargetException e) {
			Throwable actual = e.getTargetException();
			if (!this.expectsException()) {
				notifier.fireTestFailure(new Failure(this.description, actual));
                this.result = TestResult.RED;
			} else if (this.isUnexpectedException(actual)) {
				String message = "Unexpected exception, expected<"
						+ this.getExpectedException().getName() + "> but was<"
						+ actual.getClass().getName() + ">";
				notifier.fireTestFailure(new Failure(this.description, new Exception(message, actual)));
                this.result = TestResult.RED;
			}
		} catch (Throwable e) {
			notifier.fireTestFailure(new Failure(this.description, e));
            this.result = TestResult.RED;
		} finally {
			notifier.fireTestFinished(description);
		}
	}

	private boolean toBeIgnored() {
		return this.javaMethod.getAnnotation(Ignore.class) != null;
	}

	private boolean isUnexpectedException(Throwable actual) {
		return this.getExpectedException() != actual.getClass();
	}

	public Object reRunTestMethod() throws Exception {
	    Constructor<?> constructor = javaMethod.getDeclaringClass().getConstructor();
	    constructor.setAccessible(true);
		Object test = constructor.newInstance();
		Object[] args = providers.getInjectionValues(policy, arity());
		return this.javaMethod.invoke(test, args);
	}


    private int arity() {
        return javaMethod.getParameterTypes().length;
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
		for (TestMethod dependency : this.providers) {
			dependency.run(notifier);
			allParentsGreen &= dependency.result == TestResult.GREEN;
		}
		if (allParentsGreen && !this.toBeIgnored()) {
			this.runTestMethod(notifier);
		} else {
			this.result = TestResult.WHITE;
			notifier.fireTestIgnored(this.description);
		}
	}

	private void runTestMethod(RunNotifier notifier) {
		try {
		    Object[] args = providers.getInjectionValues(policy, arity());
			this.invokeMethod(newTestCase(), notifier, args);
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



    public void validate() {
        if (!javaMethod.isAnnotationPresent(Test.class)) {
            context.throwNewError("Method %s is not a test method, missing @Test annotation.", toString());
        }
        int d = providers.size();
        int p = arity();
        if (p > 0 && p != d) {
            context.throwNewError("Method %s has %d parameters but %d dependencies.", toString(), p, d);
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
                context.throwNewError("Parameter (%s) in (%s) is not assignable from depedency (%s).",
                        t, getJavaMethod(), tm.getJavaMethod());
            }
            if (tm.expectsException()) {
                context.throwNewError("(%s): invalid dependency (%s), provider must not expect exception.",
                        getJavaMethod(), tm.getJavaMethod());
            }
        }
    }
    

}

/**
 * The states, a {@link TestMethod} can have.
 * 
 * @author Lea Haensenberger
 */
enum TestResult {
	GREEN, NOT_YET_RUN, RED, WHITE
}
