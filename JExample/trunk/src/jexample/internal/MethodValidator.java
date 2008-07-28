package jexample.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jexample.Depends;

import org.junit.Test;
import org.junit.internal.runners.InitializationError;


/**
 * The <code>MethodValidator</code> class validates all test methods in
 * <code>testClass</code>.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class MethodValidator {

	private final List<Throwable> fErrors = new ArrayList<Throwable>();

	private Set<Method> testMethods;

	private final TestClass testClass;

	/**
	 * @param news
	 *            a {@link Set} of {@link Method} under test
	 * @param testClass
	 *            the {@link TestClass} to be run
	 */
	public MethodValidator( Set<Method> news, TestClass testClass ) {
		testMethods = news;
		this.testClass = testClass;
	}

	private void validateInstanceMethods() {
		validateTestMethods();

		Set<Method> methods = testMethods;
		if ( methods.size() == 0 )
			fErrors.add( new Exception( "No runnable methods" ) );
	}

	/**
	 * Checks, if there is a default constructor, if there are test methods and
	 * if they are all public and if their declaring classes are also public.
	 * In the end the declared dependencies are validated. 
	 * 
	 * @return a {@link List} of all encountered errors
	 */
	public List<Throwable> validateMethodsForComposedRunner() {
		validateNoArgConstructor();
		validateInstanceMethods();
		validateDependencies();
		return fErrors;
	}

	/**
	 * Checks if the list of errors is empty, if not, an {@link InitializationError} is thrown
	 * @throws InitializationError
	 */
	public void assertValid() throws InitializationError {
		if ( !fErrors.isEmpty() )
			throw new InitializationError( fErrors );
	}

	private void validateNoArgConstructor() {
		try {
			this.testClass.getConstructor();
		} catch ( Exception e ) {
			fErrors.add( new Exception( "Test class should have public zero-argument constructor", e ) );
		}
	}

	private void validateTestMethods() {
		Set<Method> methods = testMethods;

		for ( Method each : methods ) {
			//if ( !Modifier.isPublic( each.getDeclaringClass().getModifiers() ) )
			//	fErrors.add( new Exception( "Class " + each.getDeclaringClass().getName() + " should be public" ) );
			if ( !Modifier.isPublic( each.getModifiers() ) )
				fErrors.add( new Exception( "Method " + each.getName() + " should be public" ) );

		}
	}

	private void validateDependencies() {
		DependencyValidator depValidator = new DependencyValidator();

		List<Method> methods = this.getAnnotatedMethodsWithDependencies();
		List<Method> dependencies = new ArrayList<Method>();
		List<Throwable> errors = new ArrayList<Throwable>();
		for ( Method each : methods ) {
			try {
				// TODO dependencies = this.testClass.getDependenciesFor( each );
			} catch ( Exception e ) {
				fErrors.add( e );
			}
			errors = depValidator.dependencyIsValid( each, dependencies.toArray( new Method[dependencies.size()] ) );
			fErrors.addAll( errors );
		}
	}

	private List<Method> getAnnotatedMethodsWithDependencies() {
		List<Method> results = new ArrayList<Method>();
		Annotation depAnnotation, testAnnotation;
		for ( Method eachMethod : this.testMethods ) {
			depAnnotation = eachMethod.getAnnotation( Depends.class );
			testAnnotation = eachMethod.getAnnotation( Test.class );
			if ( depAnnotation != null && testAnnotation != null ) {
				results.add( eachMethod );
			}
		}
		return results;
	}
}
