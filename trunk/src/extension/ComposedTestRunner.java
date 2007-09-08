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
 * @author Lea Hänsenberger Date: Sep 7, 2007
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

	private List<Method> getTestMethods() {
		return this.getAnnotatedMethods( MyTest.class );
	}

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

	protected String testName( Method method ) {
		return method.getName();
	}

	protected Annotation[] testAnnotations( Method method ) {
		return method.getAnnotations();
	}

	protected String getName() {
		return this.testClass.getName();
	}

	protected Annotation[] classAnnotations() {
		return this.testClass.getAnnotations();
	}

	protected Object createTest() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
	        InvocationTargetException, NoSuchMethodException {
		return this.testClass.getConstructor().newInstance();
	}

	public Class<?> getTestClass() {
		return this.testClass;
	}

}
