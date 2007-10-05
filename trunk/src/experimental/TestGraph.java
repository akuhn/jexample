package experimental;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public class TestGraph {

	private static TestGraph graph;

	private Set<TestClass> classesUnderTest;

	private Map<Method, TestMethod> testMethods;

	private TestGraph() {
		this.classesUnderTest = new HashSet<TestClass>();
		this.testMethods = new HashMap<Method, TestMethod>();
	}

	public static TestGraph getInstance() {
		if ( graph == null ) {
			graph = new TestGraph();
		}

		return graph;
	}

	public void addClass( TestClass testClass ) throws InitializationError {

		CycleDetector detector = new CycleDetector( testClass );
		List<Method> methodsUnderTest;
		try {
			methodsUnderTest = detector.checkCyclesAndGetMethods();
		} catch ( InitializationError e ) {
			throw e;
		} catch ( Throwable e ) {
			throw new InitializationError( e );
		}

		this.validate( methodsUnderTest, testClass ); // validate the methods of the testClass

		// if everything is fine, add the testClass to the list of classes under test
		// and add all the testmethods and its dependencies to the list of testmethods
		this.classesUnderTest.add( testClass );

		this.addTestMethods( methodsUnderTest, testClass );

		// add dependencies to the testMethods
		this.addDependencies( methodsUnderTest, testClass );

	}

	public Description descriptionForClass( TestClass testClass ) {
		Description description = Description.createSuiteDescription( testClass.getJavaClass() );
		for ( TestMethod method : this.testMethods.values() ) {
			description.addChild( method.createDescription() );

		}
		return description;
	}

	public void runClass( TestClass testClass, RunNotifier notifier ) {
		for ( TestMethod method : this.testMethods.values() ) {
			if ( method.belongsToClass( testClass ) ) {
				method.run( notifier );
			}
		}
	}

	private void validate( List<Method> methodsUnderTest, TestClass testClass ) throws InitializationError {
		MethodValidator validator = new MethodValidator( methodsUnderTest, testClass );
		validator.validateMethodsForDefaultRunner();
		validator.assertValid();
	}

	private void addTestMethods( List<Method> methodsUnderTest, TestClass testClass ) throws InitializationError {
		TestMethod testMethod;
		for ( Method method : methodsUnderTest ) {
			testMethod = this.addTestMethod( method );
			try {
				this.addTestMethods( testMethod.extractDependencies( testClass ), testClass );
			} catch ( Exception e ) {
				throw new InitializationError( e );
			}
		}
	}

	private TestMethod addTestMethod( Method method ) {
		TestMethod testMethod;
		if ( !this.testMethods.containsKey( method ) ) {
			testMethod = new TestMethod( method );
			this.testMethods.put( method, testMethod );
		} else {
			testMethod = this.testMethods.get( method );
		}

		return testMethod;
	}

	private void addDependencies( List<Method> methodsUnderTest, TestClass testClass ) throws InitializationError {
		List<Method> deps = new ArrayList<Method>();
		TestMethod testMethod;
		for ( Method method : methodsUnderTest ) {
			testMethod = this.testMethods.get( method );
			try {
				deps = testMethod.extractDependencies( testClass );
			} catch ( Exception e ) {
				throw new InitializationError( e );
			}

			for ( Method dep : deps ) {
				testMethod.addDependency( this.testMethods.get( dep ) );
			}
		}
	}

	/**
	 * Only for testing purposes
	 * @return a {@link Map} with the mapping {@link Method} -&gt; {@link TestMethod}
	 */
	public Map<Method, TestMethod> getTestMethods() {
		return this.testMethods;
	}

	/**
	 * Only for testing purposes
	 * @return a {@link Set} of {@link TestClass} Objects
	 */
	public Set<TestClass> getClasses() {
		return this.classesUnderTest;
	}

}
