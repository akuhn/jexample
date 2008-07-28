package jexample.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jexample.Depends;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * A wrapper for the {@link Class} under test.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class TestClass {
	private final Class<?> fClass;

	/**
	 * @param klass the {@link Class} under test
	 */
	public TestClass( Class<?> klass ) {
		fClass = klass;
	}

	/**
	 * @return a {@link List} of all {@link Method}'s annotated with {@link Test}
	 */
	public List<Method> getTestMethods() {
		return getAnnotatedMethods( Test.class );
	}

	List<Method> getBefores() {
		return getAnnotatedMethods( BeforeClass.class );
	}

	List<Method> getAfters() {
		return getAnnotatedMethods( AfterClass.class );
	}

	/**
	 * @return the {@link Constructor} of <code>fClass</code>
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public Constructor<?> getConstructor() throws SecurityException, NoSuchMethodException {
		return fClass.getDeclaredConstructor();
	}

	/**
	 * @return the {@link Class} object of <code>fClass</code>
	 */
	public Class<?> getJavaClass() {
		return fClass;
	}

	/**
	 * @return the name of <code>fClass</code>
	 */
	public String getName() {
		return fClass.getName();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( Object obj ) {
		return this.fClass.equals( ( ( TestClass ) obj ).fClass );
	}

	/**
	 * @param testMethod the {@link Method} whos dependencies have to be returned
	 * @return a {@link List} of {@link Method}'s <code>testMethod</code> depends on
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	public List<Method> getDependenciesFor( Method testMethod ) throws NoSuchMethodException, SecurityException,
			ClassNotFoundException {
		DependencyParser parser = new DependencyParser( this );
		List<Method> deps = new ArrayList<Method>();
		Depends annotation = testMethod.getAnnotation( Depends.class );
		if ( annotation != null ) {
			deps = parser.getDependencies( ( ( Depends ) annotation ).value() );
		}
		return deps;
	}

	private List<Method> getAnnotatedMethods( Class<? extends Annotation> annotationClass ) {
		List<Method> results = new ArrayList<Method>();
		for ( Class<?> eachClass : getSuperClasses( fClass ) ) {
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

	private boolean runsTopToBottom( Class<? extends Annotation> annotation ) {
		return annotation.equals( Before.class ) || annotation.equals( BeforeClass.class );
	}

	private boolean isShadowed( Method method, List<Method> results ) {
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

	private List<Class<?>> getSuperClasses( Class<?> testClass ) {
		ArrayList<Class<?>> results = new ArrayList<Class<?>>();
		Class<?> current = testClass;
		while ( current != null ) {
			results.add( current );
			current = current.getSuperclass();
		}
		return results;
	}
}
