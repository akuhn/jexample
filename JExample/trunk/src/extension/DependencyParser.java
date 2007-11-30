/**
 * 
 */
package extension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

// enum PrimitiveDataTypes {
// INT( int.class ), LONG( long.class ), DOUBLE( double.class ), FLOAT(
// float.class ), CHAR( char.class ), BOOLEAN(
// boolean.class );
//
// private final Class< ?> clazz;
//
// private PrimitiveDataTypes( Class< ?> clazz ) {
// this.clazz = clazz;
// }
//
// public Class< ?> getClazz() {
// return this.clazz;
// }
// }

/**
 * This class gets the value from the <code>Annotation Depends</code> and
 * extracts method names and parameters. with this information it creates a
 * <code>List</code> of <code>Method Objects</code> which represent the
 * dependencies of the current test method.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class DependencyParser {

	private String annotationValue;

	private final TestClass testClass;

	private List<Method> dependencies;

	private Method method;

	public DependencyParser( TestClass myTestClass ) {
		this.annotationValue = "";
		this.testClass = myTestClass;
	}

	/**
	 * This methods parses the <code>String annotationValue</code> and
	 * extracts method names and the parameters of this method, if there are
	 * overloaded methods. With the extracted information it creates
	 * <code>Method</code> Objects.
	 * 
	 * @param method
	 * 
	 * @return a <code>List</code> of <code>Method</code> Objects which are
	 *         created from the <code>String value</code>.
	 * 
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

	public List<Method> getDependencies( Method javaMethod ) throws NoSuchMethodException {
		this.dependencies = new ArrayList<Method>();
		Method methodBefore = this.testClass.getMethodBefore( javaMethod );
		if ( methodBefore != null ) {
			this.dependencies.add( methodBefore );
		} else {
			throw new NoSuchMethodException( "There is no method declared before " + javaMethod.getName() );
		}
		return this.dependencies;
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
			// clazz = this.testClass.getJavaClass();
			clazz = this.method.getDeclaringClass();
		}

		return clazz;
	}

	private void addDependency( Class<?> clazz, String dependency, String[] parameters ) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException {
		Method dep = clazz.getMethod( this.extractName( dependency ), this.getParameterClasses( parameters ) );
		if ( !this.dependencies.contains( dep ) ) {
			this.dependencies.add( dep );
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
			return params.split( "," );
		} else {
			return new String[0];
		}
	}

	private String[] getMethodNames() {
		return this.annotationValue.split( ";" );
	}

}
