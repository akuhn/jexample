package extension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Assume.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class MyMethodRoadie {
	private final Object fTest;

	private final RunNotifier fNotifier;

	private final Description fDescription;

	private MyTestMethod fTestMethod;

	public MyMethodRoadie( Object test, MyTestMethod method, RunNotifier notifier, Description description ) {
		fTest = test;
		fNotifier = notifier;
		fDescription = description;
		fTestMethod = method;
	}

	public void run() {
		if ( fTestMethod.isIgnored() ) {
			fNotifier.fireTestIgnored( fDescription );
			return;
		}
		fNotifier.fireTestStarted( fDescription );
		try {
			runTest();
		} finally {
			fNotifier.fireTestFinished( fDescription );
		}
	}

	public void runTest() {
		runBeforesThenTestThenAfters( new Runnable() {
			public void run() {
				runTestMethod();
			}
		} );
	}

	public void runBeforesThenTestThenAfters( Runnable test ) {
		try {
			runBefores();
			test.run();
		} catch ( MyFailedBefore e ) {
		} catch ( Exception e ) {
			throw new RuntimeException( "test should never throw an exception to this level" );
		} finally {
			runAfters();
		}
	}

	protected void runTestMethod() {
		try {
			fTestMethod.invoke( fTest );

		} catch ( InvocationTargetException e ) { // wrapper-Exception, wraps an Exception throw
													// by an invoked method or constructor
			Throwable actual = e.getTargetException();
			if ( actual instanceof AssumptionViolatedException )
				return;
		} catch ( Throwable e ) {
			addFailure( e );
		}
	}

	private void runBefores() throws MyFailedBefore {
		try {
			List<Method> befores = fTestMethod.getBefores();
			for ( Method before : befores )
				before.invoke( fTest );
		} catch ( InvocationTargetException e ) {
			addFailure( e.getTargetException() );
			throw new MyFailedBefore();
		} catch ( Throwable e ) {
			addFailure( e );
			throw new MyFailedBefore();
		}
	}

	private void runAfters() {
		List<Method> afters = fTestMethod.getAfters();
		for ( Method after : afters )
			try {
				after.invoke( fTest );
			} catch ( InvocationTargetException e ) {
				addFailure( e.getTargetException() );
			} catch ( Throwable e ) {
				addFailure( e ); // Untested, but seems impossible
			}
	}

	protected void addFailure( Throwable e ) {
		fNotifier.fireTestFailure( new Failure( fDescription, e ) );
	}
}
