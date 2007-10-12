package extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.InitializationError;

import extension.annotations.Depends;
import extension.annotations.MyTest;

public class MethodValidator {

	private final List<Throwable> fErrors = new ArrayList<Throwable>();

	private List<Method> testMethods;

	private final TestClass testClass;

	public MethodValidator( List<Method> methodsUnderTest, TestClass testClass ) {
		testMethods = methodsUnderTest;
		this.testClass = testClass;
	}

	public void validateInstanceMethods() {
		validateTestMethods();

		List<Method> methods = testMethods;
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
		List<Method> methods = testMethods;

		for ( Method each : methods ) {
			if ( !Modifier.isPublic( each.getDeclaringClass().getModifiers() ) )
				fErrors.add( new Exception( "Class " + each.getDeclaringClass().getName() + " should be public" ) );
			if ( !Modifier.isPublic( each.getModifiers() ) )
				fErrors.add( new Exception( "Method " + each.getName() + " should be public" ) );

		}
	}

	private void validateDependencies() {
		DependencyParser parser = new DependencyParser( this.testClass );
		DependencyValidator depValidator = new DependencyValidator();
		List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();
		annotations.add( Depends.class );
		annotations.add( MyTest.class );

		List<Method> methods = this.getAnnotatedMethods( annotations );
		List<Method> dependencies = new ArrayList<Method>();
		List<Throwable> errors = new ArrayList<Throwable>();
		for ( Method each : methods ) {
			try {
				dependencies = parser.getDependencies( each.getAnnotation( Depends.class ).value() );
			} catch ( Exception e ) {
				fErrors.add( e );
			}
			errors = depValidator.dependencyIsValid( each, dependencies.toArray( new Method[dependencies.size()] ) );
			fErrors.addAll( errors );
		}
	}

	private List<Method> getAnnotatedMethods( List<Class<? extends Annotation>> annotations ) {
		List<Method> results = new ArrayList<Method>();
		Annotation annotation;
		for ( Method eachMethod : this.testMethods ) {
			boolean nullAnnotation = false;
			for ( Class<? extends Annotation> annotationClass : annotations ) {
				annotation = eachMethod.getAnnotation( annotationClass );
				if ( annotation == null ) {
					nullAnnotation = true;
					break;
				}
			}
			// if there are superclasses, whose testmethods are overwritten, isShadowed()
			// checks, if there
			// are overwritten methods, those are not added to the results list, so only
			// the "lowest" submethod is added
			if ( !nullAnnotation )
				results.add( eachMethod );
		}
		// if ( runsTopToBottom( annotationClass ) )
		// Collections.reverse( results );
		return results;
	}
}
