/**
 * 
 */
package extension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class MethodCollector {

	private final TestClass testClass;
	private Map<Method,TestMethod> methods;
	private Set<Method> newMethods;
	private final Map<Method,TestMethod> alreadyCollectedMethods;

	public MethodCollector( TestClass testClass, Map<Method,TestMethod> alreadyCollectedMethods ) {
		this.testClass = testClass;
		this.alreadyCollectedMethods = alreadyCollectedMethods;
		this.methods = new HashMap<Method,TestMethod>();
		this.addMethodsToMap( this.testClass.getTestMethods() );
		this.newMethods = new HashSet<Method>();
	}

	private void addMethodsToMap( Collection<Method> methodsToAdd ) {
		for ( Method method : methodsToAdd ) {
			this.addTestMethod( method );
		}
	}

	public Map<Method,TestMethod> collectTestMethods() throws SecurityException, NoSuchMethodException,
			ClassNotFoundException {
		this.newMethods.addAll( this.methods.keySet() );
		Set<Method> toIterate = new HashSet<Method>();
		do {
			toIterate.clear();
			toIterate.addAll( this.newMethods );
			this.newMethods.clear();

			this.processMethods( toIterate );
		} while ( !this.newMethods.isEmpty() );

		return this.methods;
	}

	private void processMethods( Set<Method> set ) throws SecurityException, NoSuchMethodException,
			ClassNotFoundException {
		List<Method> deps = new ArrayList<Method>();
		for ( Method method : set ) {
			deps = this.testClass.getDependenciesFor( method );
			for ( Method depMethod : deps ) {
				this.addMethod( method, depMethod );
			}
		}

	}

	private void addMethod( Method method, Method depMethod ) {
		if ( !this.methods.containsKey( depMethod ) ) {
			this.newMethods.add( depMethod );
			this.addTestMethod( depMethod );
		}
		this.methods.get( method ).addDependency( this.methods.get( depMethod ) );
	}

	private void addTestMethod( Method depMethod ) {
		if ( !this.alreadyCollectedMethods.containsKey( depMethod ) ) {
			this.methods.put( depMethod, new TestMethod( depMethod ) );
		} else {
			this.methods.put( depMethod, this.alreadyCollectedMethods.get( depMethod ) );
		}
	}
	
}
