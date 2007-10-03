/**
 * 
 */
package experimental;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * This class gets the value from the <code>Annotation Depends</code> and extracts method
 * names and parameters. with this information it creates a <code>List</code> of <code>Method Objects</code>
 * which represent the dependencies of the current test method.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class DependencyParser {

	private String annotationValue;

	private final TestClass testClass;

	private List<Method> dependencies;

	public DependencyParser( TestClass myTestClass ) {
		this.annotationValue = "";
		this.testClass = myTestClass;
	}

	/**
	 * This methods parses the <code>String annotationValue</code> and extracts method names
	 * and the parameters of this method, if there are overloaded methods. With the extracted
	 * information it creates <code>Method</code> Objects.
	 * 
	 * @return a <code>List</code> of <code>Method</code> Objects which are created from the
	 * <code>String value</code>.
	 * 
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public List<Method> getDependencies( String value ) throws ClassNotFoundException, SecurityException, NoSuchMethodException {
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
			// remove parameters, because there could be a class declaration, too
			dependency = dependency.substring( 0, index );
		}
		Class<?> clazz;
		if ( ( index = dependency.lastIndexOf( "." ) ) > -1 ) {
			try {
	            clazz = Class.forName( this.testClass.getJavaClass().getPackage() + dependency.substring( 0, index ) );
            } catch ( ClassNotFoundException e ) {
	            throw new ClassNotFoundException("The class "+ this.testClass.getJavaClass().getPackage() + dependency.substring( 0, index ) + " was not found.");
            }
		} else {
			clazz = this.testClass.getJavaClass();
		}

		return clazz;
	}

	private void addDependency( Class<?> clazz, String dependency, String[] parameters ) throws ClassNotFoundException, SecurityException,
	        NoSuchMethodException {
		Method dep = clazz.getMethod( this.extractName( dependency ), this.getParameterClasses( parameters ) );
		if ( !this.dependencies.contains( dep ) ) {
			this.dependencies.add( dep );
		}
	}

	private String extractName( String dependency ) {
		int index;
		if ( ( index = dependency.indexOf( "(" ) ) > -1 ) {
			return dependency.substring( 0, index );
		} else {
			return dependency;
		}
	}

	private Class<?>[] getParameterClasses( String[] parameters ) throws ClassNotFoundException {
		Class<?>[] newParams = new Class<?>[parameters.length];
		for ( int i = 0; i < parameters.length; i++ ) {
			newParams[i] = Class.forName( parameters[i].trim() );
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
