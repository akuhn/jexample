package extension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class MyClassRoadie {
	private RunNotifier fNotifier;

	private MyTestClass fTestClass;

	private Description fDescription;

	private final Runnable fRunnable;

	public MyClassRoadie( RunNotifier notifier, MyTestClass testClass, Description description, Runnable runnable ) {
		fNotifier = notifier;
		fTestClass = testClass;
		fDescription = description;
		fRunnable = runnable;
	}

	protected void runUnprotected() {
		fRunnable.run();
	};

	protected void addFailure( Throwable targetException ) {
		fNotifier.fireTestFailure( new Failure( fDescription, targetException ) );
	}

	public void runProtected() {
		try {
			runBefores(); // beforeClass
			runUnprotected();
		} catch ( MyFailedBefore e ) {
		} finally {
			runAfters(); // afterClass
		}
	}

	private void runBefores() throws MyFailedBefore {
		try {
			List<Method> befores = fTestClass.getBefores();
			for ( Method before : befores )
				before.invoke( null );
		} catch ( InvocationTargetException e ) {
			addFailure( e.getTargetException() );
			throw new MyFailedBefore();
		} catch ( Throwable e ) {
			addFailure( e );
			throw new MyFailedBefore();
		}
	}

	private void runAfters() {
		List<Method> afters = fTestClass.getAfters();
		for ( Method after : afters )
			try {
				after.invoke( null );
			} catch ( InvocationTargetException e ) {
				addFailure( e.getTargetException() );
			} catch ( Throwable e ) {
				addFailure( e ); // Untested, but seems impossible
			}
	}
}