/**
 * 
 */
package extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import extension.annotations.MyTest;

/**
 * @author Lea Haensenberger 
 * Date: Sep 7, 2007
 * 
 */

public class ComposedTestRunner extends Runner {

	private Class<?> testClass;

	private final List<Method> testMethods;

	public ComposedTestRunner( Class<?> toTest ) {
		this.testClass = toTest;
		this.testMethods = this.getTestMethods();
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
	public void run( RunNotifier notifier ) {
		Object test;
		try {
			test = this.createTest();
		} catch ( Throwable e ) {
			// TODO failure handling
			e.printStackTrace();
			return;
		}
		this.runTestMethods( test );
	}

	/**
	 * Iterate over all testmethods and run them.
	 * @param test
	 */
	private void runTestMethods( Object test ) {
		for ( Method method : this.testMethods ) {
			try {
				method.invoke( test );
			} catch ( Throwable e ) {
				// TODO failure handling
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get all methods with the Annotation <code>MyTest</code>
	 * @return
	 */
	private List<Method> getTestMethods() {
		return this.getAnnotatedMethods( MyTest.class );
	}

	/**
	 * Get all methods annotated with <code>annotationClass</code>
	 * @param annotationClass
	 * @return
	 */
	private List<Method> getAnnotatedMethods( Class<? extends Annotation> annotationClass ) {
		List<Method> results = new ArrayList<Method>();
		Method[] methods = this.testClass.getDeclaredMethods();
		for ( Method eachMethod : methods ) {
			Annotation annotation = eachMethod.getAnnotation( annotationClass );
			if ( annotation != null )
				results.add( eachMethod );
		}

		return results;
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
		return this.testClass.getAnnotations();
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
		return this.testClass;
	}

}
