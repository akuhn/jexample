package extension.old;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.InitializationError;

import extension.annotations.MyTest;

/**
 * This class validates the specified dependencies between tests.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class DependencyValidator {

	private final List<Throwable> fErrors = new ArrayList<Throwable>();

	
	/**
	 * If the number of parameters of <code>method</code> is greater than 0 the number
	 * of parameters and the number of <code>dependencies</code> have to be the same.
	 * If <code>method</code> has no parameters all the <code>dependencies</code> have to have return type
	 * void.
	 * 
	 * @param method the <code>Method</code> that has dependencies
	 * @param dependencies the dependencies of <code>method</code>
	 * @return <code>true</code>, if all the dependencies are valid, <code>false</code> otherwise
	 * @throws InitializationError 
	 */
	public List<Throwable> dependencyIsValid( Method method, Method... dependencies ) {
		this.validateDependencies( method, dependencies );
		return this.fErrors;
	}

	private void validateDependencies( Method method, Method[] dependencies ) {
		this.assertDependenciesAreTestMethods( dependencies );
		Class<?>[] params = method.getParameterTypes();
		if ( params.length > 0 ) {
			if ( params.length != dependencies.length ) {
				this.fErrors.add( new Exception( "Method " + method.getName() + " has not same number of parameters and dependencies." ) );
			} else {
				this.compareTypes( dependencies, params );
			}
		} else {
			this.assertVoidReturnTypes( dependencies );
		}
	}

	private void assertDependenciesAreTestMethods( Method[] dependencies ) {
		MyTest annotation;
		for ( Method method : dependencies ) {
			annotation = method.getAnnotation( MyTest.class );
			if ( annotation == null ) {
				this.fErrors.add( new Exception( "Dependency " + method.getName() + " is not a test method." ) );
			}
		}
	}

	private void assertVoidReturnTypes( Method[] dependencies ) {
		for ( Method method : dependencies ) {
			if ( method.getReturnType() != Void.TYPE ) {
				this.fErrors.add( new Exception( "Method " + method.getName() + " has a return type other than void." ) );
			}
		}
	}

	private void compareTypes( Method[] dependencies, Class<?>[] params ) {
		for ( int i = 0; i < params.length; i++ ) {
			Class<?> returnType = dependencies[i].getReturnType();
			if ( !params[i].equals( returnType ) ) {
				this.fErrors.add( new Exception( "Parameter (" + params[i].getName()
				        + ") is not of the same type as the return type of the dependency (" + returnType.getName() + ")" ) );
			}
		}
	}

}
