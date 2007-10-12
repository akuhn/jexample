package experimental;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import extension.annotations.Depends;

enum TestResult {
	NOT_YET_RUN, GREEN, RED, IGNORED
}

public class TestMethod {

	private Method javaMethod;

	// has to be a list, because the order is important
	private List< TestMethod> dependencies;

	private TestResult state;

	private Object returnValue;

	public TestMethod( Method method ) {
		this.javaMethod = method;
		this.dependencies = new ArrayList< TestMethod>();
		this.state = TestResult.NOT_YET_RUN;
	}

	public List< Method> extractDependencies( TestClass testClass ) throws SecurityException, ClassNotFoundException,
			NoSuchMethodException {
		List< Method> deps = new ArrayList< Method>();
		Depends annotation = this.javaMethod.getAnnotation( Depends.class );
		if ( annotation != null ) {
			deps = new DependencyParser( testClass ).getDependencies( annotation.value() );
		}
		return deps;
	}

	public boolean belongsToClass( TestClass testClass ) {
		return this.javaMethod.getDeclaringClass().equals( testClass.getJavaClass() );
	}

	public void run( RunNotifier notifier ) {
		if ( this.hasBeenRun() )
			return;
		boolean allParentsGreen = true;
		for ( TestMethod dependency : this.dependencies ) {
			dependency.run( notifier );
			allParentsGreen = allParentsGreen && dependency.isGreen();
		}
		if ( allParentsGreen && !this.isIgnoredByAnnotation() ) {
			this.runTestMethod( notifier );
		} else {
			this.setIgnored();
			notifier.fireTestIgnored( this.createDescription() );
		}
	}

	public boolean equals( Object obj ) {
		return this.javaMethod.equals( ( ( TestMethod ) obj ).javaMethod );
	}

	public void addDependency( TestMethod testMethod ) {
		if ( !this.dependencies.contains( testMethod ) ) {
			this.dependencies.add( testMethod );
		}
	}

	public List< TestMethod> getDependencies() {
		return this.dependencies;
	}

	public Description createDescription() {
		return Description.createTestDescription( this.javaMethod.getDeclaringClass(), this.javaMethod.getName(),
				this.javaMethod.getAnnotations() );
	}

	private void runTestMethod( RunNotifier notifier ) {
		Description description = this.createDescription();
		Object test;
		try {
			test = this.javaMethod.getDeclaringClass().getConstructor().newInstance();
		} catch ( InvocationTargetException e ) {
			notifier.testAborted( description, e.getCause() );
			return;
		} catch ( Exception e ) {
			notifier.testAborted( description, e );
			return;
		}
		this.invokeMethod( test, description, notifier, this.getArguments() );
	}

	private Object[] getArguments() {
		Class< ?>[] paramTypes = this.javaMethod.getParameterTypes();
		Object[] arguments = new Object[paramTypes.length];
		for ( int i = 0; i < paramTypes.length; i++ ) {
			if ( this.dependencies.get( i ).returnValue != null
					&& paramTypes[i].equals( this.dependencies.get( i ).returnValue.getClass() ) ) {
				arguments[i] = this.dependencies.get( i ).returnValue;
			}
		}

		return arguments;
	}

	private void invokeMethod( Object test, Description description, RunNotifier notifier, Object... args ) {
		notifier.fireTestStarted( description );
		try {
			this.returnValue = this.javaMethod.invoke( test, args );
			this.setGreen();
		} catch ( InvocationTargetException e ) {
			this.addFailure( e.getTargetException(), notifier, description );
		} catch ( Throwable e ) {
			this.addFailure( e, notifier, description );
		} finally {
			notifier.fireTestFinished( description );
		}
	}

	private void addFailure( Throwable e, RunNotifier notifier, Description description ) {
		notifier.fireTestFailure( new Failure( description, e ) );
		this.setFailed();
	}

	private boolean isIgnoredByAnnotation() {
		return this.javaMethod.getAnnotation( Ignore.class ) != null;
	}

	private void setGreen() {
		this.state = TestResult.GREEN;
	}

	private void setIgnored() {
		this.state = TestResult.IGNORED;
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

	public Class< ?> getDeclaringClass() {
		return this.javaMethod.getDeclaringClass();
	}

}
