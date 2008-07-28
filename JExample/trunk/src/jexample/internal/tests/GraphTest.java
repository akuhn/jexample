package jexample.internal.tests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Map;

import jexample.Depends;
import jexample.internal.TestClass;
import jexample.internal.TestGraph;
import jexample.internal.TestMethod;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;


public class GraphTest {

	private TestGraph graph;

	@Before
	public void setUp() throws Exception {
		graph = new TestGraph();
	}

	@Test
	public void testAddOneClass() throws InitializationError {
		graph.add( OneClass.class );
		assertEquals( 1, graph.getClasses().size() );
		assertEquals( 4, graph.getTestMethods().size() );
	}

	@Test
	public void testAddMethodsOfOneClass() throws InitializationError {
		graph.add( OneClass.class );
		assertEquals( 4, graph.getTestMethods().size() );
	}

	@Test
	public void testAddDependenciesOfOneClass() throws InitializationError, SecurityException, NoSuchMethodException {
		graph.add( OneClass.class );
		assertEquals( 0, graph.getTestMethod( OneClass.class.getMethod( "testMethod" ) ).getDependencies().size() );
		assertEquals( 1, graph.getTestMethod( OneClass.class.getMethod( "anotherTestMethod" ) ).getDependencies().size() );
		assertEquals( 1, graph.getTestMethod( OneClass.class.getMethod( "depOnOtherTest" ) ).getDependencies().size() );
		assertEquals( 0, graph.getTestMethod( B.class.getMethod( "otherTest" ) ).getDependencies().size() );
	}

	@Test( expected = InitializationError.class )
	public void detectCycles() throws InitializationError {
		graph.add( Cyclic.class );
	}

	static private class OneClass {

		public OneClass() {

		}

		@Test
		public void testMethod() {

		}

		@Test
		@Depends( "testMethod" )
		public void anotherTestMethod() {

		}

		@Test
		@Depends( "B.otherTest" )
		public void depOnOtherTest() {

		}
	}

	static private class Cyclic {
		public Cyclic() {

		}

		@Test
		public void testMethod() {

		}

		@Test
		@Depends( "testMethod;depOnOtherTest" )
		public void anotherTestMethod() {

		}

		@Test
		@Depends( "anotherTestMethod" )
		public void depOnOtherTest() {

		}
	}

	static private class CyclicOverClasses {
		public CyclicOverClasses() {

		}

		@Test
		public void testMethod() {

		}

		@Test
		@Depends( "testMethod" )
		public void anotherTestMethod() {

		}

		@Test
		@Depends( "B.otherTestCyclic" )
		public void depOnOtherTest() {

		}
	}
}
