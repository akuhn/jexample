package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;

public class JUnit4ClassRunner extends Runner implements Filterable, Sortable {
	private final List<Method> fTestMethods;

	private TestClass fTestClass;

	public JUnit4ClassRunner( Class<?> klass ) throws InitializationError {
		fTestClass = new TestClass( klass );
		fTestMethods = getTestMethods();
		validate();
	}

	protected List<Method> getTestMethods() {
		return fTestClass.getTestMethods();
	}

	protected void validate() throws InitializationError {
		MethodValidator methodValidator = new MethodValidator( fTestClass );
		methodValidator.validateMethodsForDefaultRunner();
		methodValidator.assertValid();
	}

	@Override
	public void run( final RunNotifier notifier ) {

		// run the beforeClasses, then the befores, then the actual methods,
		// then the afters and finally the afterClasses
		new ClassRoadie( notifier, fTestClass, getDescription(),
		        new Runnable() {
			        public void run() {
				        runMethods( notifier );
			        }
		        } ).runProtected();
	}

	protected void runMethods( final RunNotifier notifier ) {
		for ( Method method : fTestMethods )
			invokeTestMethod( method, notifier );
	}

	@Override
	public Description getDescription() {
		Description spec = Description.createSuiteDescription( getName(),
		        classAnnotations() );
		List<Method> testMethods = fTestMethods;
		for ( Method method : testMethods )
			spec.addChild( methodDescription( method ) );
		return spec;
	}

	protected Annotation[] classAnnotations() {
		return fTestClass.getJavaClass().getAnnotations();
	}

	protected String getName() {
		return getTestClass().getName();
	}

	protected Object createTest() throws Exception {
		return getTestClass().getConstructor().newInstance();
	}

	protected void invokeTestMethod( Method method, RunNotifier notifier ) {
		Description description = methodDescription( method );
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
		TestMethod testMethod = wrapMethod( method );
		new MethodRoadie( test, testMethod, notifier, description ).run();
	}

	protected TestMethod wrapMethod( Method method ) {
		return new TestMethod( method, fTestClass );
	}

	protected String testName( Method method ) {
		return method.getName();
	}

	protected Description methodDescription( Method method ) {
		return Description.createTestDescription(
		        getTestClass().getJavaClass(), testName( method ),
		        testAnnotations( method ) );
	}

	protected Annotation[] testAnnotations( Method method ) {
		return method.getAnnotations();
	}

	public void filter( Filter filter ) throws NoTestsRemainException {
		for ( Iterator<Method> iter = fTestMethods.iterator(); iter.hasNext(); ) {
			Method method = iter.next();
			if ( !filter.shouldRun( methodDescription( method ) ) )
				iter.remove();
		}
		if ( fTestMethods.isEmpty() )
			throw new NoTestsRemainException();
	}

	public void sort( final Sorter sorter ) {
		Collections.sort( fTestMethods, new Comparator<Method>() {
			public int compare( Method o1, Method o2 ) {
				return sorter.compare( methodDescription( o1 ),
				        methodDescription( o2 ) );
			}
		} );
	}

	protected TestClass getTestClass() {
		return fTestClass;
	}
}