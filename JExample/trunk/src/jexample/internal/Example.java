package jexample.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Stack;

import jexample.Depends;
import jexample.InjectionPolicy;
import jexample.internal.JExampleError.Kind;

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
 * to declare {@link jexample.JExample JExampleRunner} as test runner.
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
    
    private JExampleError errors;
    private TestResult result;
    private InjectionPolicy policy;

    
	public Example(Method jmethod) {
	    assert jmethod != null;
	    jmethod.setAccessible(true);
        this.jmethod = jmethod;
        this.providers = new Dependencies();
        this.result = TestResult.VIRGIN;
        this.description = Description.createTestDescription(jmethod.getDeclaringClass(), jmethod.getName());
        this.returnValue = new ReturnValue(this);
        this.policy = jmethod.getDeclaringClass().getAnnotation(InjectionPolicy.class);
        this.errors = new JExampleError();
    }

	
	public Method[] collectDependencies() {
        Depends a = jmethod.getAnnotation( Depends.class );
        if (a != null) {
            try {
                DependsParser p = new DependsParser(jmethod.getDeclaringClass());
                return p.collectProviderMethods(a.value());
            } catch (InvalidDeclarationError ex) {
                errors.add(Kind.INVALID_DEPENDS_DECLARATION, ex);
            } catch (SecurityException ex) {
                errors.add(Kind.PROVIDER_NOT_FOUND, ex);
            } catch (ClassNotFoundException ex) {
                errors.add(Kind.PROVIDER_NOT_FOUND, ex);
            } catch (NoSuchMethodException ex) {
                errors.add(Kind.PROVIDER_NOT_FOUND, ex);
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
		return result != TestResult.VIRGIN;
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
		if (!errors.isEmpty()) {
		    notifier.fireTestStarted(description);
		    notifier.fireTestFailure(new Failure(description, errors));
		    notifier.fireTestFinished(description);
		    return;
		}
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
            errors.add(Kind.MISSING_TEST_ANNOTATION, "Method %s is not a test method, missing @Test annotation.", toString());
        }
        int d = providers.size();
        int p = arity();
        if (p > d) {
            errors.add(Kind.MISSING_PROVIDERS, "Method %s has %d parameters but only %d dependencies.", toString(), p, d);
        }
        else {
            validateDependencyTypes();
        }
        //if (providers.transitiveClosure().contains(this)) {
        //    errors.add(Kind.RECURSIVE_DEPENDENCIES, "Recursive dependency found.");
        //}
    }


    private void validateDependencyTypes() {
        Iterator<Example> tms = this.providers.iterator();
        int position = 1;
        for (Class<?> t : jmethod.getParameterTypes()) {
            Example tm = tms.next();
            Class<?> r = tm.jmethod.getReturnType();
            if (!t.isAssignableFrom(r)) {
                errors.add(Kind.PARAMETER_NOT_ASSIGNABLE,
                        "Parameter #%d in (%s) is not assignable from depedency (%s).",
                        position, jmethod, tm.jmethod);
            }
            if (tm.expectsException()) {
                errors.add(Kind.PROVIDER_EXPECTS_EXCEPTION,
                        "(%s): invalid dependency (%s), provider must not expect exception.", jmethod, tm.jmethod);
            }
            position++;
        }
    }


    public void errorPartOfCycle(Stack<Example> cycle) {
        errors.add(Kind.RECURSIVE_DEPENDENCIES, "Part of a cycle!");
    }
    
}

/**
 * The states, a {@link Example} can have.
 * 
 * @author Lea Haensenberger
 */
enum TestResult {
	GREEN, VIRGIN, RED, WHITE
}
