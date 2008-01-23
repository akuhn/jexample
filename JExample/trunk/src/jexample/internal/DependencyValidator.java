package jexample.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * The <code>DependencyValidator</code> class validates the specified dependencies between tests.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class DependencyValidator {

	private final List<Throwable> fErrors = new ArrayList<Throwable>();

	/**
	 * The following checks are made and have to be passed:
	 * - all the dependencies have to be test methods
	 * - a {@link Method} cannot have itself as a dependency
	 * - if <code>method</code> takes arguments, the number of dependencies have to be
	 *   the same and all the dependencies have to return the appropriate object
	 * 
	 * @param method
	 *            the <code>Method</code> that has dependencies
	 * @param dependencies
	 *            the dependencies of <code>method</code>
	 * @return <code>true</code>, if all the dependencies are valid,
	 *         <code>false</code> otherwise
	 */
	public List<Throwable> dependencyIsValid( Method method, Method... dependencies ) {
		this.validateDependencies( method, dependencies );
		return this.fErrors;
	}

	private void validateDependencies( Method method, Method[] dependencies ) {
		this.assertDependenciesAreTestMethods( dependencies );
		this.assertHasNotItselfAsDependency( method, dependencies );

		Class<?>[] params = method.getParameterTypes();
		if ( params.length > 0 ) {
			if ( params.length != dependencies.length ) {
				this.fErrors.add( new Exception( "Method " + method.getName()
						+ " has not same number of parameters and dependencies." ) );
			} else {
				this.compareTypes( dependencies, params );
			}
		}
	}

	private void assertHasNotItselfAsDependency( Method method, Method[] dependencies ) {
		if ( Arrays.asList( dependencies ).contains( method ) ) {
			this.fErrors.add( new Exception( "The method " + method.getName() + " depends on itself." ) );
		}
	}

	private void assertDependenciesAreTestMethods( Method[] dependencies ) {
		Test annotation;
		for ( Method method : dependencies ) {
			annotation = method.getAnnotation( Test.class );
			if ( annotation == null ) {
				this.fErrors.add( new Exception( "Dependency " + method.getName() + " is not a test method." ) );
			}
		}
	}

	private void compareTypes( Method[] dependencies, Class<?>[] params ) {
		for ( int i = 0; i < params.length; i++ ) {
			Class<?> returnType = dependencies[i].getReturnType();
			if ( !params[i].equals( returnType ) ) {
				this.fErrors.add( new Exception( "Parameter (" + params[i].getName()
						+ ") is not of the same type as the return type of the dependency (" + returnType.getName()
						+ ")" ) );
			}
		}
	}

}
