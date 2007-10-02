package experimental.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;

import experimental.Graph;
import experimental.TestClass;
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
	public void testAddClass() throws InitializationError {
		graph.addClass( this.testClass );
		assertEquals(2, graph.getTestMethods().size());
	}

	@MyTest
	public void testMethod() {

	}

	@MyTest
	@Depends( "testMethod" )
	public void anotherTestMethod() {

	}

}
