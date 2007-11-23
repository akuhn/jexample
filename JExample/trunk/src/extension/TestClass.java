package extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import extension.annotations.Depends;
import extension.annotations.DependsAbove;
import extension.annotations.MyTest;

public class TestClass {
	private final Class< ?> fClass;

	public TestClass( Class< ?> klass ) {
		fClass = klass;
	}

	List< Method> getTestMethods() {
		return getAnnotatedMethods( MyTest.class );
	}

	List< Method> getBefores() {
		return getAnnotatedMethods( BeforeClass.class );
	}

	List< Method> getAfters() {
		return getAnnotatedMethods( AfterClass.class );
	}

	public List< Method> getAnnotatedMethods( Class< ? extends Annotation> annotationClass ) {
		List< Method> results = new ArrayList< Method>();
		for ( Class< ?> eachClass : getSuperClasses( fClass ) ) {
			Method[] methods = eachClass.getDeclaredMethods();
			for ( Method eachMethod : methods ) {
				Annotation annotation = eachMethod.getAnnotation( annotationClass );
				// if there are superclasses, whose testmethods are overwritten,
				// isShadowed()
				// checks, if there
				// are overwritten methods, those are not added to the results
				// list, so only
				// the "lowest" submethod is added
				if ( annotation != null && !isShadowed( eachMethod, results ) )
					results.add( eachMethod );
			}
		}
		if ( runsTopToBottom( annotationClass ) )
			Collections.reverse( results );
		return results;
	}

	public List< Method> getAnnotatedMethods( List< Class< ? extends Annotation>> annotationClasses ) {
		List< Method> results = new ArrayList< Method>();
		Annotation annotation;
		for ( Class< ?> eachClass : getSuperClasses( fClass ) ) {
			Method[] methods = eachClass.getDeclaredMethods();
			for ( Method eachMethod : methods ) {
				boolean nullAnnotation = false;
				for ( Class< ? extends Annotation> annotationClass : annotationClasses ) {
					annotation = eachMethod.getAnnotation( annotationClass );
					if ( annotation == null ) {
						nullAnnotation = true;
						break;
					}
				}
				// if there are superclasses, whose testmethods are overwritten,
				// isShadowed()
				// checks, if there
				// are overwritten methods, those are not added to the results
				// list, so only
				// the "lowest" submethod is added
				if ( !nullAnnotation && !isShadowed( eachMethod, results ) )
					results.add( eachMethod );
			}
		}
		// if ( runsTopToBottom( annotationClass ) )
		// Collections.reverse( results );
		return results;
	}

	private boolean runsTopToBottom( Class< ? extends Annotation> annotation ) {
		return annotation.equals( Before.class ) || annotation.equals( BeforeClass.class );
	}

	private boolean isShadowed( Method method, List< Method> results ) {
		for ( Method each : results ) {
			if ( isShadowed( method, each ) )
				return true;
		}
		return false;
	}

	private boolean isShadowed( Method current, Method previous ) {
		if ( !previous.getName().equals( current.getName() ) )
			return false;
		if ( previous.getParameterTypes().length != current.getParameterTypes().length )
			return false;
		for ( int i = 0; i < previous.getParameterTypes().length; i++ ) {
			if ( !previous.getParameterTypes()[i].equals( current.getParameterTypes()[i] ) )
				return false;
		}
		return true;
	}

	private List< Class< ?>> getSuperClasses( Class< ?> testClass ) {
		ArrayList< Class< ?>> results = new ArrayList< Class< ?>>();
		Class< ?> current = testClass;
		while ( current != null ) {
			results.add( current );
			current = current.getSuperclass();
		}
		return results;
	}

	public Constructor< ?> getConstructor() throws SecurityException, NoSuchMethodException {
		return fClass.getConstructor();
	}

	public Class< ?> getJavaClass() {
		return fClass;
	}

	public String getName() {
		return fClass.getName();
	}

	public boolean equals( Object obj ) {
		return this.fClass.equals( ( ( TestClass ) obj ).fClass );
	}

	/**
	 * Iterates over all <code>methods</code> annotated with {@link MyTest}
	 * and gets the method declared before <code>javaMethod</code>.
	 * 
	 * @param javaMethod
	 *            the {@link Method} which depends on the {@link Method}
	 *            declared before it
	 * @return the {@link Method} declared before <code>javaMethod</code>
	 */
	public Method getMethodBefore( Method javaMethod ) {
		List< Method> methods = this.getTestMethods();
		Method before = null;
		for ( Method method : methods ) {
			if ( method.equals( javaMethod ) && before != null ) {
				return before;
			}
			before = method;
		}
		return null;
	}

	public List< Method> getDependenciesFor( Method testMethod ) throws NoSuchMethodException, SecurityException,
			ClassNotFoundException {
		DependencyParser parser = new DependencyParser( this );
		List< Method> deps = new ArrayList< Method>();
		Annotation annotation = this.getDependencyAnnotationFor( testMethod );
		if ( annotation != null ) {
			if ( this.annotationHasValue( annotation ) ) {
				deps = parser.getDependencies( ( ( Depends ) annotation ).value(), testMethod );
			} else {
				deps = parser.getDependencies( testMethod );
			}
		}
		return deps;
	}

	/**
	 * Checks if there is an {@link Annotation} that defines a dependency. If
	 * yes, this {@link Annotation} is returned.
	 * 
	 * @param testMethod
	 * 
	 * @return <code>annotation</code> if it defines a depency and
	 *         <code>null</code> otherwise
	 */
	public Annotation getDependencyAnnotationFor( Method testMethod ) {
		Annotation[] annotations = testMethod.getAnnotations();
		for ( Annotation annotation : annotations ) {
			if ( this.isDependencyAnnotation( annotation ) ) {
				return annotation;
			}
		}
		return null;
	}

	private boolean isDependencyAnnotation( Annotation annotation ) {
		return annotation.annotationType().equals( Depends.class )
				|| annotation.annotationType().equals( DependsAbove.class );
	}

	private boolean annotationHasValue( Annotation annotation ) {
		return annotation.annotationType().equals( Depends.class );
	}

}
