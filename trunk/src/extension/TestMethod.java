package extension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

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

	/**
	 * @param testClass
	 *            the {@link TestClass} the method is declared in
	 * @return a {@link List} of {@link Method}'s, which are the dependencies
	 *         of this {@link TestMethod}
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 */
	public List< Method> extractDependencies( TestClass testClass ) throws SecurityException, ClassNotFoundException,
			NoSuchMethodException {
//		List< Method> deps = new ArrayList< Method>();
//		DependencyParser parser = new DependencyParser( testClass );
//		Annotation annotation = this.getDependencyAnnotation();
//		// Depends annotation = this.javaMethod.getAnnotation( Depends.class );
//		if ( annotation != null ) {
//			if ( this.annotationHasValue( annotation ) ) {
//				deps = parser.getDependencies( ( ( Depends ) annotation ).value() );
//			} else {
//				deps = parser.getDependencies( this.javaMethod );
//			}
//		}
		return testClass.getDependenciesFor( this.javaMethod );
	}

	/**
	 * Checks, if this {@link TestMethod} belongs to <code>testClass</code>
	 * 
	 * @param testClass
	 *            the {@link TestClass} to be compared
	 * @return true, if the {@link TestMethod} belongs to <code>testClass</code>,
	 *         false otherwise
	 */
	public boolean belongsToClass( TestClass testClass ) {
		return this.javaMethod.getDeclaringClass().equals( testClass.getJavaClass() );
	}

	/**
	 * Runs this {@link TestMethod} after it run all of its dependencies.
	 * 
	 * @param notifier
	 *            the {@link RunNotifier}
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( Object obj ) {
		return this.javaMethod.equals( ( ( TestMethod ) obj ).javaMethod );
	}

	/**
	 * If the TestMethod doesn't already have the dependency
	 * <code>testMethod</code>, <code>testMethod</code> is added as a
	 * dependency.
	 * 
	 * @param testMethod
	 *            the {@link TestMethod} to be added as a dependency
	 */
	public void addDependency( TestMethod testMethod ) {
		if ( !this.dependencies.contains( testMethod ) ) {
			this.dependencies.add( testMethod );
		}
	}

	/**
	 * @return a {@link List} of {@link TestMethod}'s, being the dependencies
	 */
	public List< TestMethod> getDependencies() {
		return this.dependencies;
	}

	/**
	 * @return the testdescription of this {@link TestMethod}
	 */
	public Description createDescription() {
		return Description.createTestDescription( this.javaMethod.getDeclaringClass(), this.javaMethod.getName(),
				this.javaMethod.getAnnotations() );
	}

	/**
	 * @return the declaring {@link Class} of <code>javaMethod</code>
	 */
	public Class< ?> getDeclaringClass() {
		return this.javaMethod.getDeclaringClass();
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
			if ( this.dependencies.get( i ).returnValue != null ) {
				if ( this.typeIsCloneable( paramTypes[i] ) ) {
					// TODO: Oct 17, 2007,5:09:57 PM: should somehow invoke
					// clone() on the returnValue
					arguments[i] = this.dependencies.get( i ).returnValue;
				} else {
					arguments[i] = this.dependencies.get( i ).returnValue;
				}
			}
		}

		return arguments;
	}

	private boolean typeIsCloneable( Class< ?> clazz ) {
		for ( Class< ?> iface : clazz.getInterfaces() ) {
			if ( iface.getClass().equals( Cloneable.class ) ) {
				return true;
			}
		}
		return false;
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

}
