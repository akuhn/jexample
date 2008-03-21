/**
 * 
 */
package jexample.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import jexample.Depends;


/**
 * The <code>DependencyParser</code> class parses a String for the {@link Method}'s it
 * represents.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 * @author Adrian Kuhn (akuhn at iam.unibe.ch)
 */
public class DependencyParser {

	private String annotationValue;

	private final TestClass testClass;

	private List<Method> dependencies;

	private Method method;

	/**
	 * @param myTestClass the {@link TestClass} that is to be run
	 */
	public DependencyParser( TestClass myTestClass ) {
		this.annotationValue = "";
		this.testClass = myTestClass;
	}


	/**
	 * Name and arguments of the {@link Method}'s defined in <code>value</code> are extracted and used
	 * to get the {@link Method} object from it's declaring class.
	 * 
	 * @param value the value from the {@link Annotation} {@link Depends}.
	 * @param method the {@link Method} that depends on the {@link Method}'s to return.
	 * @return a {@link List} of the {@link Method}'s <code>method</code> depends on.
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public List<Method> getDependencies( String value, Method method ) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException {
		this.method = method;
		this.annotationValue = value;
		this.dependencies = new ArrayList<Method>();

		String[] methodNames = this.getMethodNames();

		for ( String dependency : methodNames ) {
			this.addDependency( this.getDeclaringClass( dependency ), dependency, this.getParameters( dependency ) );
		}

		return dependencies;
	}

	private Class<?> getDeclaringClass( String dependency ) throws ClassNotFoundException {
		int index;
		if ( ( index = dependency.indexOf( "(" ) ) > -1 ) {
			// remove parameters, because there could be a class declaration,
			// too
			dependency = dependency.substring( 0, index );
		}
		Class<?> clazz;
		if ( ( index = dependency.lastIndexOf( "." ) ) > -1 ) {
			String className = dependency.substring( 0, index );
			if ( ( index = className.indexOf( "." ) ) == -1 ) {
				className = this.testClass.getJavaClass().getPackage().getName() + "." + className;
			}
			try {
				clazz = Class.forName( className );
			} catch ( ClassNotFoundException e ) {
				throw new ClassNotFoundException( "The class " + className + " was not found." );
			}
		} else {
			clazz = this.method.getDeclaringClass();
		}

		return clazz;
	}

	/** Find matching method for dependency declaration.
	 * 
	 * @param clazz 
	 * @param dependency
	 * @param parameters
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	private void addDependency( Class<?> clazz, String dependency, String[] parameters )
			throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		Method found = searchMethod(clazz, this.extractName(dependency), parameters);
		this.dependencies.add( found );
	}

	private Method searchMethod(Class receiver, String name, String[] parameters)
			throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		if (parameters == null) {
			Method found = null;
			for (Method m : receiver.getMethods()) { 
				if (m.getName().equals(name)) {
					 if (found != null) throw new NoSuchMethodException(
							 "Ambigous depedency, please specify parameters: "
							 + receiver.getName() + "." + m.getName());
					 found = m;
				}
			}
			return found;
		}
		else {
			return receiver.getMethod(name, this.getParameterClasses(parameters));
		}
	}
	
	private String extractName( String dependency ) {
		int index;

		if ( ( index = dependency.indexOf( "(" ) ) > -1 ) {
			dependency = dependency.substring( 0, index );
		}

		if ( ( index = dependency.lastIndexOf( "." ) ) > -1 ) {
			dependency = dependency.substring( index + 1 );
		}

		return dependency;
	}

	private Class<?>[] getParameterClasses( String[] parameters ) throws ClassNotFoundException {
		Class<?>[] newParams = new Class<?>[parameters.length];
		for ( int i = 0; i < parameters.length; i++ ) {
			try {
				newParams[i] = Class.forName( parameters[i].trim() );
			} catch ( ClassNotFoundException e ) {
				if ( parameters[i].equals( "int" ) ) {
					newParams[i] = int.class;
				} else if ( parameters[i].equals( "long" ) ) {
					newParams[i] = long.class;
				} else if ( parameters[i].equals( "double" ) ) {
					newParams[i] = double.class;
				} else if ( parameters[i].equals( "float" ) ) {
					newParams[i] = float.class;
				} else if ( parameters[i].equals( "char" ) ) {
					newParams[i] = char.class;
				} else if ( parameters[i].equals( "boolean" ) ) {
					newParams[i] = boolean.class;
				} else {
					throw e;
				}
			}
		}
		return newParams;
	}

	private String[] getParameters( String dependency ) {
		int index;
		if ( ( index = dependency.indexOf( "(" ) ) > -1 ) {
			String params = dependency.substring( index + 1, dependency.length() - 1 );
			if (params.length() == 0) return new String[0];
			return params.split( "," );
		} else {
			return null;
		}
	}

	private String[] getMethodNames() {
		return this.annotationValue.split( ";" );
	}

}
