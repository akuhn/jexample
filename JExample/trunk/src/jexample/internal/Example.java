package jexample.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import jexample.Depends;
import jexample.InjectionPolicy;
import jexample.internal.InvalidExampleError.Kind;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * A test method with dependencies and return value. Test methods are written
 * source code to illustrate the usage of the unit under test, and may return
 * a characteristic instance of its unit under test. Thus, test methods are
 * in fact <i>examples</i> of the unit under test.
 * <p> 
 * When executing an example method, the JExample framework caches its return
 * value. If an example method declares dependencies and has arguments, the
 * framework will inject the cache return values of the dependencies as
 * parameters into the method execution. For more details, please refer to
 * {@link jexample.InjectionPolicy @InjectionPolicy}.
 * <p>
 * An example method must have at least a {@link org.junit.Test @Test} annotation.
 * The enclosing class must use an {@link org.junit.RunWith @RunWith} annotation
 * to declare {@link jexample.JExampleRunner JExampleRunner} as test runner.
 * <p>
 * An example method may return an instance of its unit under test.
 * <p>
 * An example method may depend on both successful execution and return value
 * of other examples. If it does, it must declare the dependencies using an
 * {@link jexample.Depends @Depends} annotation. An example methods with
 * dependencies may have method parameters. The number of parameters must be
 * less than or equal to the number of dependencies. The type of the n-th
 * parameter must match the return type of the n-th dependency.
 *   
 *   
 * @author Lea Haensenberger
 * @author Adrian Kuhn
 * 
 */
public class Example {

    public final Description description;
    public final ReturnValue returnValue;
    public final Method jmethod;
    public final Dependencies providers;
    
	private final ExampleGraph context;
    private TestResult result;
    private InjectionPolicy policy;

    
	public Example(Method jmethod, ExampleGraph graph) {
	    assert jmethod != null && graph != null;
	    jmethod.setAccessible(true);
        this.jmethod = jmethod;
        this.providers = new Dependencies();
        this.result = TestResult.NOT_YET_RUN;
        this.description = Description.createTestDescription(jmethod.getDeclaringClass(), jmethod.getName());
        this.context = graph;
        this.returnValue = new ReturnValue(this);
        this.policy = jmethod.getDeclaringClass().getAnnotation(InjectionPolicy.class);
    }


	public Method[] collectDependencies() {
        Depends a = jmethod.getAnnotation( Depends.class );
        if (a != null) {
            try {
                DependsParser p = new DependsParser(jmethod.getDeclaringClass());
                return p.collectProviderMethods(a.value());
            } catch (InvalidDeclarationError ex) {
                context.throwNewError(Kind.INVALID_DEPENDS_DECLARATION, ex);
            } catch (SecurityException ex) {
                context.throwNewError(Kind.PROVIDER_NOT_FOUND, ex);
            } catch (ClassNotFoundException ex) {
                context.throwNewError(Kind.PROVIDER_NOT_FOUND, ex);
            } catch (NoSuchMethodException ex) {
                context.throwNewError(Kind.PROVIDER_NOT_FOUND, ex);
            }
        }
        return new Method[0];
    }

	private boolean expectsException() {
		return this.getExpectedException() != null;
	}


	private Class<? extends Throwable> getExpectedException() {
		Test a = this.jmethod.getAnnotation(Test.class);
		if (a == null) return null;
		if (a.expected() == org.junit.Test.None.class) return null;
		return a.expected();
	}


	private boolean hasBeenRun() {
		return result != TestResult.NOT_YET_RUN;
	}

	public boolean wasSuccessful() {
	    return result == TestResult.GREEN;
	}
	
	private void invokeMethod(Object test, RunNotifier notifier, Object... args) {
		notifier.fireTestStarted(description);
		try {
			returnValue.assign(this.jmethod.invoke(test, args));
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
		return this.jmethod.getAnnotation(Ignore.class) != null;
	}

	private boolean isUnexpectedException(Throwable actual) {
		return this.getExpectedException() != actual.getClass();
	}

	public Object reRunTestMethod() throws Exception {
		Object test = newTestClassInstance();
		Object[] args = providers.getInjectionValues(policy, arity());
		return this.jmethod.invoke(test, args);
	}


    private int arity() {
        return jmethod.getParameterTypes().length;
    }

	/**
	 * Runs this {@link Example} after it run all of its dependencies.
	 * 
	 * @param notifier
	 *            the {@link RunNotifier}
	 */
	public void run(RunNotifier notifier) {
		if (this.hasBeenRun()) return;
		boolean allParentsGreen = true;
		for (Example dependency : this.providers) {
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
			this.invokeMethod(newTestClassInstance(), notifier, args);
		} catch (InvocationTargetException e) {
			notifier.testAborted(description, e.getCause());
		} catch (Exception e) {
			notifier.testAborted(description, e);
		}
	}

    private Object newTestClassInstance() throws NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        return TestClass.getConstructor(jmethod.getDeclaringClass()).newInstance();
    }



    public void validate() {
        if (!jmethod.isAnnotationPresent(Test.class)) {
            context.throwNewError(Kind.MISSING_TEST_ANNOTATION, "Method %s is not a test method, missing @Test annotation.", toString());
        }
        int d = providers.size();
        int p = arity();
        if (p > d) {
            context.throwNewError(Kind.MISSING_PROVIDERS, "Method %s has %d parameters but only %d dependencies.", toString(), p, d);
        }
        else {
            validateDependencyTypes();
        }
    }


    private void validateDependencyTypes() {
        Iterator<Example> tms = this.providers.iterator();
        int position = 1;
        for (Class<?> t : jmethod.getParameterTypes()) {
            Example tm = tms.next();
            Class<?> r = tm.jmethod.getReturnType();
            if (!t.isAssignableFrom(r)) {
                context.throwNewError(Kind.PARAMETER_NOT_ASSIGNABLE,
                        "Parameter #%d in (%s) is not assignable from depedency (%s).",
                        position, jmethod, tm.jmethod);
            }
            if (tm.expectsException()) {
                context.throwNewError(Kind.PROVIDER_EXPECTS_EXCEPTION,
                        "(%s): invalid dependency (%s), provider must not expect exception.", jmethod, tm.jmethod);
            }
            position++;
        }
    }
    
}

/**
 * The states, a {@link Example} can have.
 * 
 * @author Lea Haensenberger
 */
enum TestResult {
	GREEN, NOT_YET_RUN, RED, WHITE
}
