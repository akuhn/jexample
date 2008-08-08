/**
 * 
 */
package jexample.internal;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import jexample.Depends;
import jexample.internal.DependsScanner.Token;


/**
 * The <code>DependencyParser</code> class parses a String for the {@link Method}'s it
 * represents.
 * 
 * @author Adrian Kuhn (akuhn at iam.unibe.ch)
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class DependsParser {

	private final Class<?> testClass;

	/**
	 * @param class1 the {@link TestClass} that is to be run
	 */
	public DependsParser(Class<?> class1) {
		this.testClass = class1;
	}


	/**
	 * Name and arguments of the providers defined in <code>value</code> are extracted and used
	 * to get the {@link Method} objects from which the specified dependent method depends.
	 * 
	 * @param value String value of the {@link Depends} annotation.
	 * @return list of all provider methods.
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public List<Method> getDependencies(String value)
			throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		LinkedList<Method> dependencies = new LinkedList<Method>();
		Token[] tokens = DependsScanner.scan(value);
		for (Token t : tokens) {
			Method found = searchMethod(t);
			dependencies.add(found);
		}
		return dependencies;
	}

	
	private Method searchMethod(Token token)
			throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		Class<?> providerClass = getProviderClass(token);
		return getProviderMethod(providerClass, token);
	}


	private Class<?> getProviderClass(Token token) throws ClassNotFoundException {
		if (token.path == null) {
			return testClass;
		}
		else if (!token.path.contains(".")) {
			String fullName = testClass.getPackage().getName() + "." + token.path;
			return Class.forName(fullName);
		}
		else {
			return Class.forName(token.path);
		}
	}

	private Method getProviderMethod(Class receiver, Token token)
			throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		if (token.args == null) {
			Method found = null;
			for (Method m : receiver.getMethods()) { 
				if (m.getName().equals(token.simple)) {
					 if (found != null) throw new NoSuchMethodException(
							 "Ambigous depedency, please specify parameters: "
							 + receiver.getName() + "." + m.getName());
					 found = m;
				}
			}
			return found;
		}
		else {
			return receiver.getMethod(token.simple, this.getParameterClasses(token.args));
		}
	}
	
	private Class<?>[] getParameterClasses( String[] parameters ) throws ClassNotFoundException {
		Class<?>[] newParams = new Class<?>[parameters.length];
		for ( int i = 0; i < parameters.length; i++ ) {
			try {
				newParams[i] = Class.forName( parameters[i] );
			} catch ( ClassNotFoundException ex ) {
				try {
					newParams[i] = Class.forName( "java.lang." + parameters[i] );
				} catch ( ClassNotFoundException ignore ) {	
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
						throw ex;
					}
				}
			}
		}
		return newParams;
	}



}
