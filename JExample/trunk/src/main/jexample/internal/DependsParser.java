/**
 * 
 */
package jexample.internal;

import java.util.ArrayList;
import java.util.LinkedList;

import jexample.internal.DependsScanner.Token;


/**
 * 
 * @author Adrian Kuhn 
 * @author Lea Haensenberger
 */
public class DependsParser {

    
    private final Class base;

    
	public DependsParser(Class base) {
		this.base = base;
	}


	public MethodReference[] collectProviderMethods(String value)
			throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		LinkedList<MethodReference> $ = new LinkedList<MethodReference>();
		Token[] tokens = DependsScanner.scan(value);
		for (Token t : tokens) {
		    MethodReference found = findProviderMethod(t);
			$.add(found);
		}
		return $.toArray(new MethodReference[$.size()]);
	}

	
	private MethodReference findProviderMethod(Token token)
			throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		Class providerClass = findProviderClass(token);
		return findProviderMethod(providerClass, token);
	}


	private Class findProviderClass(Token token) throws ClassNotFoundException {
	    if (token.path == null) return base;
	    String name = token.path;
	    Class $ = findClass(name);
	    if ($ != null) return $;
        name = base.getName() + "$" + token.path;
        $ = findClass(name);
        if ($ != null) return $;
	    name = base.getPackage().getName() + "." + token.path;
        $ = findClass(name);
	    if ($ != null) return $;
		throw new ClassNotFoundException(token.path);
	}
	
	private static Class findClass(String fullname) {
	    try {
            return Class.forName(fullname);
        } 
        catch (ClassNotFoundException _) {
            return null;
        }
	}

	private MethodReference findProviderMethod(Class receiver, Token token)
			throws ClassNotFoundException, SecurityException, NoSuchMethodException {
		if (token.args == null) {
		    MethodReference found = null;
			for (MethodReference m : MethodReference.all(receiver)) { 
				if (m.equals(receiver, token.simple)) {
					 if (found != null) throw new NoSuchMethodException(
							 "Ambigous depedency, please specify parameters: "
							 + receiver.getName() + "." + m.getName());
					 found = m;
				}
			}
			if (found == null) throw new NoSuchMethodException(token.toString());
			return found;
		}
		else {
			return new MethodReference(receiver, 
			        receiver.getMethod(token.simple, this.getParameterClasses(token.args)));
		}
	}
	
	private Class[] getParameterClasses( String[] parameters ) throws ClassNotFoundException {
		ArrayList<Class> $ = new ArrayList();
		for (String name : parameters) {
		    Class c = findClass(name);
		    if (c == null) {
		        c = findClass("java.lang." + name);
		        if (c == null) {
		            if ( name.equals( "int" ) ) {
        				c = int.class;
        			} else if ( name.equals( "long" ) ) {
        				c = long.class;
        			} else if ( name.equals( "double" ) ) {
        				c = double.class;
        			} else if ( name.equals( "float" ) ) {
        				c = float.class;
        			} else if ( name.equals( "char" ) ) {
        				c = char.class;
        			} else if ( name.equals( "boolean" ) ) {
        				c = boolean.class;
        			} else {
        				throw new ClassNotFoundException(name);
        			}
				}
			}
		    $.add(c);
		}
		return $.toArray(new Class[$.size()]);
	}



}
