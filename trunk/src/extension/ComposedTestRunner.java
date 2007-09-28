/**
 * 
 */
package extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import extension.graph.TestGraph;
import extension.graph.TestNode;
import extension.graph.exception.GraphCyclicException;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 * 
 */

public class ComposedTestRunner extends Runner {

	private MyTestClass testClass;

	private final List<Method> testMethods;

	private TestGraph testGraph;

	public ComposedTestRunner( Class<?> toTest ) throws InitializationError {
		this.testClass = new MyTestClass( toTest );
		this.testMethods = this.getTestMethods();
		this.validate();
		this.createTestGraph();
	}

	private void createTestGraph() throws InitializationError {
		try {
			this.testGraph = new TestGraph( this.wrapMethods(), this.testClass );
		} catch ( SecurityException e ) {
			throw new InitializationError( "Error while initializing TestGraph" );
		} catch ( NoSuchMethodException e ) {
			throw new InitializationError( "Error while initializing TestGraph" );
		} catch ( ClassNotFoundException e ) {
			throw new InitializationError( "Error while initializing TestGraph" );
		} catch ( GraphCyclicException e ) {
			throw new InitializationError( e );
		}

	}

	private List<MyTestMethod> wrapMethods() throws SecurityException, ClassNotFoundException, NoSuchMethodException, InitializationError {
		List<MyTestMethod> methods = new ArrayList<MyTestMethod>();

		for ( Method each : this.testMethods ) {
			methods.add( this.wrapMethod( each ) );
		}
		return methods;
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
		for ( TestNode node : this.testGraph.getSortedNodes() ) {
			this.invokeTestMethod( node, notifier );
		}
	}

	protected void invokeTestMethod( TestNode node, RunNotifier notifier ) {
		Description description = methodDescription( node.getTestMethod().getMethod() );
		Object test;
		try {
			test = createTest();
		} catch ( InvocationTargetException e ) {
			notifier.testAborted( description, e.getCause() );
			return;
		} catch ( Exception e ) {
			notifier.testAborted( description, e );
			return;
		}
		new MyMethodRoadie( test, node, notifier, description ).run();
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
