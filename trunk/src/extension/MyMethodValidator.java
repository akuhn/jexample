package extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.internal.runners.InitializationError;

import extension.annotations.Depends;
import extension.annotations.MyTest;
import extension.parser.DependencyParser;

public class MyMethodValidator {

	private final List<Throwable> fErrors = new ArrayList<Throwable>();

	private MyTestClass fTestClass;

	public MyMethodValidator( MyTestClass testClass ) {
		fTestClass = testClass;
	}

	public void validateInstanceMethods() {
		validateTestMethods( After.class, false );
		validateTestMethods( Before.class, false );
		validateTestMethods( MyTest.class, false );

		List<Method> methods = fTestClass.getAnnotatedMethods( MyTest.class );
		if ( methods.size() == 0 )
			fErrors.add( new Exception( "No runnable methods" ) );
	}

	public void validateStaticMethods() {
		validateTestMethods( BeforeClass.class, true );
		validateTestMethods( AfterClass.class, true );
	}

	public List<Throwable> validateMethodsForDefaultRunner() {
		validateNoArgConstructor();
		validateStaticMethods();
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
			fTestClass.getConstructor();
		} catch ( Exception e ) {
			fErrors.add( new Exception( "Test class should have public zero-argument constructor", e ) );
		}
	}

	private void validateTestMethods( Class<? extends Annotation> annotation, boolean isStatic ) {
		List<Method> methods = fTestClass.getAnnotatedMethods( annotation );

		for ( Method each : methods ) {
			if ( Modifier.isStatic( each.getModifiers() ) != isStatic ) {
				String state = isStatic ? "should" : "should not";
				fErrors.add( new Exception( "Method " + each.getName() + "() " + state + " be static" ) );
			}
			if ( !Modifier.isPublic( each.getDeclaringClass().getModifiers() ) )
				fErrors.add( new Exception( "Class " + each.getDeclaringClass().getName() + " should be public" ) );
			if ( !Modifier.isPublic( each.getModifiers() ) )
				fErrors.add( new Exception( "Method " + each.getName() + " should be public" ) );

		}
	}

	private void validateDependencies() {
		DependencyParser parser;
		DependencyValidator depValidator = new DependencyValidator();
		List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();
		annotations.add( Depends.class );
		annotations.add( MyTest.class );
		
		List<Method> methods = fTestClass.getAnnotatedMethods( annotations );
		List<Method> dependencies = new ArrayList<Method>();
		for ( Method each : methods ) {
			parser = new DependencyParser( each.getAnnotation( Depends.class ).value(), fTestClass );
			try {
	            dependencies = parser.getDependencies();
	            depValidator.dependencyIsValid( each, dependencies.toArray( new Method[dependencies.size()] ) );
            } catch ( Exception e ) {
	            fErrors.add( e );
            }
		}
	}
}
