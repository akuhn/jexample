package extension;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

/**
 * The <code>TestGraph</code> class takes the responsibility delegated from
 * {@link ComposedTestRunner}: validating {@link Method}'s, running tests and
 * returning {@link Description}'s.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class TestGraph {

	private static TestGraph graph;

	private Set<TestClass> classesUnderTest;

	private Map<Method,TestMethod> testMethods;

	public TestGraph() {
		this.classesUnderTest = new HashSet<TestClass>();
		this.testMethods = new HashMap<Method,TestMethod>();
	}

	public static TestGraph getInstance() {
		if ( graph == null ) {
			graph = new TestGraph();
		}

		return graph;
	}

	/**
	 * All {@link Method}'s are collected, checked for cycles, validated and
	 * then added to {@link Map} of all the {@link Method}'s to be run.
	 * 
	 * @param testClass
	 *            the {@link TestClass} to be added
	 * @throws InitializationError
	 */
	public void addClass( TestClass testClass ) throws InitializationError {

		Map<Method,TestMethod> methods = this.collectMethods( testClass );

		this.detectCycles( methods.values() );

		this.validate( methods.keySet(), testClass ); // validate the methods
		// of the testClass
		this.classesUnderTest.add( testClass );

		for ( Method method : methods.keySet() ) {
			if ( !this.testMethods.containsKey( method ) ) {
				this.testMethods.put( method, methods.get( method ) );
			}
		}
	}

	/**
	 * The {@link Description}'s for the {@link Method}'s of this class are
	 * added as children to the class description. If there are dependencies
	 * from {@link TestMethod}'s which are not declared in a {@link TestClass}
	 * that is run in this turn, the {@link Description} of the declaring
	 * {@link Class} is also added as a child.
	 * 
	 * @param testClass
	 *            the {@link TestClass} to get the {@link Description} from
	 * @return the <code>description</code> for <code>testClass</code>;
	 */
	public Description descriptionForClass( TestClass testClass ) {
		Description description = Description.createSuiteDescription( testClass.getJavaClass() );
		Set<Description> subDescriptions = new HashSet<Description>();
		for ( TestMethod method : this.testMethods.values() ) {
			if ( method.belongsToClass( testClass ) ) {
				description.addChild( method.createDescription() );
			} else if ( this.methodBelongsToNoClass( method ) ) {
				subDescriptions = this.addChildDescription( subDescriptions, method );
			}
	
		}
		for ( Description subDescription : subDescriptions ) {
			description.addChild( subDescription );
		}
		return description;
	}

	/**
	 * All {@link TestMethod}'s of <code>testClass</code> are run, inclusive
	 * their dependencies.
	 * 
	 * @param testClass
	 *            the {@link TestClass} to be run
	 * @param notifier
	 *            {@link RunNotifier}
	 */
	public void runClass( TestClass testClass, RunNotifier notifier ) {
		for ( TestMethod method : this.testMethods.values() ) {
			if ( method.belongsToClass( testClass ) ) {
				method.run( notifier );
			}
		}
	}

	private void detectCycles( Collection<TestMethod> methods ) throws InitializationError {
		CycleDetector detector = new CycleDetector( methods );

		if ( detector.hasCycle() ) {
			throw new InitializationError( "The dependencies are cyclic." );
		}
	}

	private Map<Method,TestMethod> collectMethods( TestClass testClass ) throws InitializationError {
		MethodCollector collector = new MethodCollector( testClass, this.testMethods );
		try {
			return collector.collectTestMethods();
		} catch ( Throwable e1 ) {
			throw new InitializationError( e1 );
		}
	}

	private Set<Description> addChildDescription( Set<Description> subDescriptions, TestMethod method ) {
		Class<?> declaringClass = method.getDeclaringClass();
		Description description;

		if ( ( description = this.getDescriptionForClass( declaringClass, subDescriptions ) ) == null ) {
			description = Description.createSuiteDescription( declaringClass );
			subDescriptions.add( description );
		}
		description.addChild( method.createDescription() );

		return subDescriptions;
	}

	private Description getDescriptionForClass( Class<?> declaringClass, Set<Description> subDescriptions ) {
		for ( Description description : subDescriptions ) {
			if ( description.getDisplayName().equals( declaringClass.getName() ) ) {
				return description;
			}
		}
		return null;
	}

	private boolean methodBelongsToNoClass( TestMethod method ) {
		for ( TestClass testClass : this.classesUnderTest ) {
			if ( method.belongsToClass( testClass ) ) {
				return false;
			}
		}
		return true;
	}

	private void validate( Set<Method> methodUnderTest, TestClass testClass ) throws InitializationError {
		MethodValidator validator = new MethodValidator( methodUnderTest, testClass );
		validator.validateMethodsForDefaultRunner();
		validator.assertValid();
	}

	/**
	 * Only for testing purposes
	 * 
	 * @return a {@link Map} with the mapping {@link Method} -&gt;
	 *         {@link TestMethod}
	 */
	public Map<Method,TestMethod> getTestMethods() {
		return this.testMethods;
	}

	/**
	 * Only for testing purposes
	 * 
	 * @return a {@link Set} of {@link TestClass} Objects
	 */
	public Set<TestClass> getClasses() {
		return this.classesUnderTest;
	}
}
