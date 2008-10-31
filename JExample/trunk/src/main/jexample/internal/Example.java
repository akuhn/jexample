
package jexample.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Stack;

import jexample.Depends;
import jexample.JExampleOptions;
import jexample.internal.JExampleError.Kind;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;


/**
 * A test method with dependencies and return value. Test methods are written
 * source code to illustrate the usage of the unit under test, and may return a
 * characteristic instance of its unit under test. Thus, test methods are in
 * fact <i>examples</i> of the unit under test.
 * <p>
 * When executing an example method, the JExample framework caches its return
 * value. If an example method declares dependencies and has arguments, the
 * framework will inject the cache return values of the dependencies as
 * parameters into the method execution. For more details, please refer to
 * {@link jexample.JExampleOptions @InjectionPolicy}.
 * <p>
 * An example method must have at least a {@link org.junit.Test @Test}
 * annotation. The enclosing class must use an {@link org.junit.RunWith
 * @RunWith} annotation to declare {@link jexample.JExample JExampleRunner} as
 * test runner.
 * <p>
 * An example method may return an instance of its unit under test.
 * <p>
 * An example method may depend on both successful execution and return value of
 * other examples. If it does, it must declare the dependencies using an
 * {@link jexample.Depends @Depends} annotation. An example methods with
 * dependencies may have method parameters. The number of parameters must be
 * less than or equal to the number of dependencies. The type of the n-th
 * parameter must match the return type of the n-th dependency.
 * @author Lea Haensenberger
 * @author Adrian Kuhn
 */
public class Example {

    public final Description     description;
    public final ReturnValue     returnValue;
    public final MethodReference method;
    public final Dependencies    providers;
    public final ExampleClass    owner;

    private JExampleError        errors;
    private ExampleState         result;
    private JExampleOptions      policy;


    public Example( MethodReference method , ExampleClass owner ) {
        assert method != null && owner != null;
        this.owner = owner;
        this.method = method;
        this.providers = new Dependencies();
        this.result = ExampleState.NONE;
        this.description = method.createTestDescription();
        this.returnValue = new ReturnValue( this );
        this.policy = method.jclass.getAnnotation( JExampleOptions.class );
        this.errors = new JExampleError();
    }

    public MethodReference[] collectDependencies() {
        Depends a = method.getAnnotation( Depends.class );
        if ( a != null ) {
            try {
                DependsParser p = new DependsParser( method.jclass );
                return p.collectProviderMethods( a.value() );
            } catch ( InvalidDeclarationError ex ) {
                errors.add( Kind.INVALID_DEPENDS_DECLARATION , ex );
            } catch ( SecurityException ex ) {
                errors.add( Kind.PROVIDER_NOT_FOUND , ex );
            } catch ( ClassNotFoundException ex ) {
                errors.add( Kind.PROVIDER_NOT_FOUND , ex );
            } catch ( NoSuchMethodException ex ) {
                errors.add( Kind.PROVIDER_NOT_FOUND , ex );
            }
        }
        return new MethodReference[0];
    }

    private boolean expectsException() {
        return this.getExpectedException() != null;
    }


    private Class<? extends Throwable> getExpectedException() {
        Test a = this.method.getAnnotation( Test.class );
        if ( a == null )
            return null;
        if ( a.expected() == org.junit.Test.None.class )
            return null;
        return a.expected();
    }


    private boolean hasBeenRun() {
        return result != ExampleState.NONE;
    }

    public boolean wasSuccessful() {
        return result == ExampleState.GREEN;
    }

    private void invokeMethod( Object test , RunNotifier notifier ,
                               Object... args ) {
        notifier.fireTestStarted( description );
        try {
            returnValue.assign( this.method.invoke( test , args ) );
            returnValue.assignInstance( test );
            this.result = ExampleState.GREEN;
        } catch ( InvocationTargetException e ) {
            Throwable actual = e.getTargetException();
            if ( !this.expectsException() ) {
                notifier.fireTestFailure( new Failure( this.description ,
                        actual ) );
                this.result = ExampleState.RED;
            } else if ( this.isUnexpectedException( actual ) ) {
                String message = "Unexpected exception, expected<"
                                 + this.getExpectedException().getName()
                                 + "> but was<" + actual.getClass().getName()
                                 + ">";
                notifier.fireTestFailure( new Failure( this.description ,
                        new Exception( message , actual ) ) );
                this.result = ExampleState.RED;
            }
        } catch ( Throwable e ) {
            notifier.fireTestFailure( new Failure( this.description , e ) );
            this.result = ExampleState.RED;
        } finally {
            notifier.fireTestFinished( description );
        }
    }


    private boolean toBeIgnored() {
        return this.method.getAnnotation( Ignore.class ) != null;
    }

    private boolean isUnexpectedException( Throwable actual ) {
        return this.getExpectedException() != actual.getClass();
    }

    public Object reRunTestMethod() throws Exception {
        owner.runBefores();
        Object test = newTestClassInstance();
        Object[] args = providers.getInjectionValues( policy , arity() );
        return this.method.invoke( test , args );
    }


    private int arity() {
        return method.arity();
    }

    /**
     * Runs this {@link Example} after it run all of its dependencies.
     * @param notifier the {@link RunNotifier}
     */
    public void run( RunNotifier notifier ) {
        owner.runBefores();
        if ( this.hasBeenRun() )
            return;
        if ( !errors.isEmpty() ) {
            notifier.fireTestStarted( description );
            notifier.fireTestFailure( new Failure( description , errors ) );
            notifier.fireTestFinished( description );
            return;
        }
        boolean allParentsGreen = true;
        for ( Example dependency : this.providers ) {
            dependency.run( notifier );
            allParentsGreen &= dependency.result == ExampleState.GREEN;
        }
        if ( allParentsGreen && !this.toBeIgnored() ) {
            try {
                Object[] args = this.providers.getInjectionValues( this.policy ,
                        this.arity() );
                this.invokeMethod( this.newTestClassInstance() , notifier ,
                        args );
            } catch ( InvocationTargetException e ) {
                notifier.testAborted( this.description , e.getCause() );
            } catch ( Exception e ) {
                notifier.testAborted( this.description , e );
            }
        } else {
            this.result = ExampleState.WHITE;
            notifier.fireTestIgnored( this.description );
        }
    }

    private Object newTestClassInstance() throws Exception {
        if (Util.cloneTestCase(this.policy) 
                && providers.hasFirstProviderImplementedIn(this)) {
            return providers.get( 0 ).returnValue.getTestCaseInstance();
        }
        return Util.getConstructor( method.jclass ).newInstance();
    }

    public void validate() {
        if ( !method.isAnnotationPresent( Test.class ) ) {
            errors
                    .add(
                            Kind.MISSING_TEST_ANNOTATION ,
                            "Method %s is not a test method, missing @Test annotation." ,
                            toString() );
        }
        int d = providers.size();
        int p = arity();
        if ( p > d ) {
            errors.add( Kind.MISSING_PROVIDERS ,
                    "Method %s has %d parameters but only %d dependencies." ,
                    toString() , p , d );
        } else {
            validateDependencyTypes();
        }
        // if (providers.transitiveClosure().contains(this)) {
        // errors.add(Kind.RECURSIVE_DEPENDENCIES,
        // "Recursive dependency found.");
        // }
    }


    private void validateDependencyTypes() {
        Iterator<Example> tms = this.providers.iterator();
        int position = 1;
        for ( Class<?> t : method.getParameterTypes() ) {
            Example tm = tms.next();
            Class<?> r = tm.method.getReturnType();
            if ( !t.isAssignableFrom( r ) ) {
                errors
                        .add(
                                Kind.PARAMETER_NOT_ASSIGNABLE ,
                                "Parameter #%d in (%s) is not assignable from depedency (%s)." ,
                                position , method , tm.method );
            }
            if ( tm.expectsException() ) {
                errors
                        .add(
                                Kind.PROVIDER_EXPECTS_EXCEPTION ,
                                "(%s): invalid dependency (%s), provider must not expect exception." ,
                                method , tm.method );
            }
            position++;
        }
    }


    public void errorPartOfCycle( Stack<Example> cycle ) {
        errors.add( Kind.RECURSIVE_DEPENDENCIES , "Part of a cycle!" );
    }

    @Override
    public String toString() {
        return "Example: " + method;
    }

    public void validateCycle() {
        providers.validateCycle( this );
    }

}
