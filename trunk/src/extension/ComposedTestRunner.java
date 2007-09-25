/**
 * 
 */
package extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 * 
 */

public class ComposedTestRunner extends Runner {

	private MyTestClass testClass;

	private final List<Method> testMethods;

	public ComposedTestRunner( Class<?> toTest ) throws InitializationError {
		this.testClass = new MyTestClass( toTest );
		this.testMethods = this.getTestMethods();
		this.validate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.Runner#getDescription()
	 */
	@Override
	public Description getDescription() {
		Description spec = Description.createSuiteDescription( getName(), classAnnotations() );
		List<Method> testMethods = this.testMethods;
		for ( Method method : testMethods )
			spec.addChild( methodDescription( method ) );
		return spec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.Runner#run(org.junit.runner.notification.RunNotifier)
	 */
	@Override
	public void run( final RunNotifier notifier ) {
		new MyClassRoadie( notifier, this.testClass, this.getDescription(), new Runnable() {

			@Override
			public void run() {
				runMethods( notifier );
			}

		} ).runProtected();
	}

	protected void runMethods( RunNotifier notifier ) {
		for ( Method method : this.testMethods ) {
			this.invokeTestMethod( method, notifier );
		}
	}

	protected void invokeTestMethod( Method method, RunNotifier notifier ) {
		Description description = methodDescription( method );
		Object test;
		MyTestMethod testMethod;
		try {
			test = createTest();
			testMethod = wrapMethod( method );
		} catch ( InvocationTargetException e ) {
			notifier.testAborted( description, e.getCause() );
			return;
		} catch ( Exception e ) {
			notifier.testAborted( description, e );
			return;
		}
		new MyMethodRoadie( test, testMethod, notifier, description ).run();
	}

	protected MyTestMethod wrapMethod( Method method ) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InitializationError {
		return new MyTestMethod( method, this.testClass );
	}

	/**
	 * Get all methods with the Annotation <code>MyTest</code>
	 * @return a <code>List</code> of methods
	 */
	private List<Method> getTestMethods() {
		return this.testClass.getTestMethods();
	}

	protected Description methodDescription( Method method ) {
		return Description.createTestDescription( getTestClass(), testName( method ), testAnnotations( method ) );
	}

	/**
	 * Get the name of <code>method</code>
	 * @param method
	 * @return the name of <code>method</code>
	 */
	protected String testName( Method method ) {
		return method.getName();
	}

	/**
	 * Get the <code>Annotations</code> of a testmethod
	 * @param method
	 * @return an <code>Array</code> of <code>Annotations</code>
	 */
	protected Annotation[] testAnnotations( Method method ) {
		return method.getAnnotations();
	}

	/**
	 * Get the name of the class under test
	 * @return the name of the class.
	 */
	protected String getName() {
		return this.testClass.getName();
	}

	/**
	 * Get the <code>Annotations</code> of the testclass
	 * @return an <code>Array</code> of <code>Annotations</code>
	 */
	protected Annotation[] classAnnotations() {
		return this.testClass.getJavaClass().getAnnotations();
	}

	/**
	 * Creates a new instance of the class under Test.
	 * @return the new instance of the testclass
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	protected Object createTest() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
	        InvocationTargetException, NoSuchMethodException {
		return this.testClass.getConstructor().newInstance();
	}

	public Class<?> getTestClass() {
		return this.testClass.getClass();
	}

	private void validate() throws InitializationError {
		MyMethodValidator validator = new MyMethodValidator( this.testClass );
		validator.validateMethodsForDefaultRunner();
		validator.assertValid();
	}
}
