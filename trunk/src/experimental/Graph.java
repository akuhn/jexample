package experimental;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import extension.annotations.MyTest;

public class Graph {

	private static Graph graph;

	private Set<TestClass> classesUnderTest;

	private Map<Method, TestMethod> testMethods;

	private Graph() {
		this.classesUnderTest = new HashSet<TestClass>();
		this.testMethods = new HashMap<Method, TestMethod>();
	}

	public static Graph getInstance() {
		if ( graph == null ) {
			graph = new Graph();
		}

		return graph;
	}

	public void addClass( TestClass testClass ) throws InitializationError {

		this.validate( testClass ); // validate the methods of the testClass

		// if everything is fine, add the testClass to the list of classes under test
		// and add all the testmethods and its dependencies to the list of testmethods
		this.classesUnderTest.add( testClass );

		// TODO: cycle detection
		this.addTestMethods( testClass.getAnnotatedMethods( MyTest.class ), testClass );
	}

	public Description descriptionForClass( TestClass testClass ) {
    	Description description = Description.createSuiteDescription( testClass.getJavaClass() );
    	for ( TestMethod method : this.testMethods.values() ) {
    		if ( method.belongsToClass( testClass ) ) {
    			description.addChild( method.createDescription() );
    		}
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

	private void validate( TestClass testClass ) throws InitializationError {
		MethodValidator validator = new MethodValidator( testClass );
		validator.validateMethodsForDefaultRunner();
		validator.assertValid();
	}

	private void addTestMethods( List<Method> methods, TestClass testClass ) throws InitializationError {
		TestMethod testMethod;
		for ( Method method : methods ) {
			testMethod = this.addTestMethod( method );
			try {
	            this.addTestMethods( testMethod.extractDependencies( testClass ), testClass );
            } catch ( Exception e ) {
	            throw new InitializationError(e);
            }
		}
	}

	private TestMethod addTestMethod( Method method ) {
		TestMethod testMethod;
	    if(!this.testMethods.containsKey( method )){
	    	testMethod = new TestMethod(method);
	    	this.testMethods.put( method, testMethod );
	    } else {
	    	testMethod = this.testMethods.get( method );
	    }
	    
	    return testMethod;
    }


	public Map<Method, TestMethod> getTestMethods() {
	    return this.testMethods;
    }

}
