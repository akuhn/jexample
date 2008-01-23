/**
 * 
 */
package jexample.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The <code>MethodCollector</code> class collects all {@link Method}'s
 * involved in a test run.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class MethodCollector {

	private final TestClass testClass;
	private Map<Method,TestMethod> methods;
	private Set<Method> newMethods;
	private final Map<Method,TestMethod> alreadyCollectedMethods;

	/**
	 * @param testClass
	 *            the {@link TestClass} the {@link Method}'s have to be
	 *            collected from
	 * @param alreadyCollectedMethods
	 *            a {@link Map} of already collected {@link Method}'s, e.g.
	 *            when a TestSuite is run
	 */
	public MethodCollector( TestClass testClass, Map<Method,TestMethod> alreadyCollectedMethods ) {
		this.testClass = testClass;
		this.alreadyCollectedMethods = alreadyCollectedMethods;
		this.methods = new HashMap<Method,TestMethod>();
		this.addMethodsToMap( this.testClass.getTestMethods() );
		this.newMethods = new HashSet<Method>();
	}

	/**
	 * All {@link Method}'s are collected in a non-recursive way, so no
	 * endless-loop is risked. For all {@link Method}'s of
	 * <code>this.testClass</code> is checked, whether the dependencies are
	 * already collected. If not, they are processed in a second loop and so on,
	 * until there are no new {@link Method}'s to process anymore.
	 * 
	 * @return a {@link Map} with all the collected {@link Method}'s as keys
	 *         and {@link TestMethod}'s as values in it
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 */
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

	private void addMethodsToMap( Collection<Method> methodsToAdd ) {
		for ( Method method : methodsToAdd ) {
			this.addTestMethod( method );
		}
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
