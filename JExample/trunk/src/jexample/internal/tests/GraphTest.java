package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import jexample.Depends;
import jexample.JExampleRunner;
import jexample.internal.ExampleGraph;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.RunWith;


public class GraphTest {

	private ExampleGraph graph;

	@Before
	public void setUp() throws Exception {
		graph = new ExampleGraph();
	}

	@Test
	public void testAddOneClass() throws InitializationError {
		graph.add( OneClass.class );
		//assertEquals( 1, graph.getClasses().size() );
		assertEquals( 4, graph.getExamples().size() );
	}

	@Test
	public void testAddMethodsOfOneClass() throws InitializationError {
		graph.add( OneClass.class );
		assertEquals( 4, graph.getExamples().size() );
	}

	@Test
	public void testAddDependenciesOfOneClass() throws InitializationError, SecurityException, NoSuchMethodException {
		graph.add( OneClass.class );
		assertEquals( 0, graph.getExample( OneClass.class.getMethod( "testMethod" ) ).providers.size() );
		assertEquals( 1, graph.getExample( OneClass.class.getMethod( "anotherTestMethod" ) ).providers.size() );
		assertEquals( 1, graph.getExample( OneClass.class.getMethod( "depOnOtherTest" ) ).providers.size() );
		assertEquals( 0, graph.getExample( B.class.getMethod( "otherTest" ) ).providers.size() );
	}

	@Test( expected = InitializationError.class )
	public void detectCycles() throws InitializationError {
		graph.add( Cyclic.class );
	}

	@RunWith(JExampleRunner.class)
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
