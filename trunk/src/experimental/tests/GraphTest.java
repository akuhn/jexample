package experimental.tests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;

import experimental.Graph;
import experimental.TestClass;
import experimental.TestMethod;
import extension.annotations.Depends;
import extension.annotations.MyTest;

public class GraphTest {

	private static Graph graph;

	private TestClass testClass;

	@Before
	public void setUp() throws Exception {
		graph = Graph.getInstance();
		this.testClass = new TestClass( this.getClass() );
	}

	@Test
	public void testAddOneClass() throws InitializationError {
		graph.addClass( this.testClass );
		assertEquals( 1, graph.getClasses().size() );
		assertEquals(2, graph.getTestMethods().size());
	}
	
	@Test
	public void testAddMethodsOfOneClass() throws InitializationError {
		graph.addClass( this.testClass );
		assertEquals(2, graph.getTestMethods().size());
	}
	
	@Test
	public void testAddDependenciesOfOneClass() throws InitializationError, SecurityException, NoSuchMethodException{
		graph.addClass( this.testClass );
		Map<Method, TestMethod> testMethods = graph.getTestMethods();
		assertEquals( 0, testMethods.get( this.getClass().getMethod( "testMethod" ) ).getDependencies().size() );
		assertEquals( 1, testMethods.get( this.getClass().getMethod( "anotherTestMethod" ) ).getDependencies().size() );
	}

	@MyTest
	public void testMethod() {

	}

	@MyTest
	@Depends( "testMethod" )
	public void anotherTestMethod() {

	}

}
