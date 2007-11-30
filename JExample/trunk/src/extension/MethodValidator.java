package extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.internal.runners.InitializationError;

import extension.annotations.MyTest;

public class MethodValidator {

	private final List<Throwable> fErrors = new ArrayList<Throwable>();

	private Set<Method> testMethods;

	private final TestClass testClass;

	public MethodValidator( Set<Method> methodUnderTest, TestClass testClass ) {
		testMethods = methodUnderTest;
		this.testClass = testClass;
	}

	public void validateInstanceMethods() {
		validateTestMethods();

		Set<Method> methods = testMethods;
		if ( methods.size() == 0 )
			fErrors.add( new Exception( "No runnable methods" ) );
	}

	public List<Throwable> validateMethodsForDefaultRunner() {
		validateNoArgConstructor();
		validateInstanceMethods();
		validateDependencies();
		return fErrors;
	}

	public void assertValid() throws InitializationError {
		if ( !fErrors.isEmpty() )
			throw new InitializationError( fErrors );
	}

	public void validateNoArgConstructor() {
		try {
			this.testClass.getConstructor();
		} catch ( Exception e ) {
			fErrors.add( new Exception( "Test class should have public zero-argument constructor", e ) );
		}
	}

	private void validateTestMethods() {
		Set<Method> methods = testMethods;

		for ( Method each : methods ) {
			if ( !Modifier.isPublic( each.getDeclaringClass().getModifiers() ) )
				fErrors.add( new Exception( "Class " + each.getDeclaringClass().getName() + " should be public" ) );
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
				dependencies = this.testClass.getDependenciesFor( each );
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
			depAnnotation = this.testClass.getDependencyAnnotationFor( eachMethod );
			testAnnotation = eachMethod.getAnnotation( MyTest.class );
			if ( depAnnotation != null && testAnnotation != null ) {
				results.add( eachMethod );
			}
		}
		return results;
	}
}
