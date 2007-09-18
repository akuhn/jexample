/**
 * 
 */
package extension.parser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import extension.MyTestClass;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 * 
 * This class gets the value from the <code>Annotation Depends</code> and extracts method
 * names and parameters. with this information it creates a <code>List</code> of <code>Method Objects</code>
 * which represent the dependencies of the current test method.
 */
public class DependencyParser {

	private final String annotationValue;

	private final MyTestClass testClass;

	private List<Method> dependencies;

	public DependencyParser( String value, MyTestClass myTestClass ) {
		this.annotationValue = value;
		this.testClass = myTestClass;

	}

	public List<Method> getDependencies() throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		this.dependencies = new ArrayList<Method>();

		String[] methodNames = this.getMethodNames();
		for ( String dependency : methodNames ) {
			String[] parameters = this.getParameters( dependency );
			this.addDependency( dependency, parameters );
		}

		return dependencies;
	}

	private void addDependency( String dependency, String[] parameters ) throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		Method dep = this.testClass.getJavaClass().getMethod( this.extractName(dependency), this.getParameterClasses( parameters ) );
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
