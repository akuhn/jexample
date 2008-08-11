package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import jexample.Depends;
import jexample.JExample;
import jexample.internal.ExampleGraph;
import jexample.internal.JExampleError;
import jexample.internal.JExampleError.Kind;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;


public class ExampleGraphTest {

	private ExampleGraph graph;

	@Before
	public void setUp() throws Exception {
		graph = new ExampleGraph();
	}

	@Test
	public void testAddOneClass() throws JExampleError {
		graph.add( OneClass.class );
		//assertEquals( 1, graph.getClasses().size() );
		assertEquals( 4, graph.getExamples().size() );
	}

	@Test
	public void testAddMethodsOfOneClass() throws JExampleError {
		graph.add( OneClass.class );
		assertEquals( 4, graph.getExamples().size() );
	}

	@Test
	public void testAddDependenciesOfOneClass() throws JExampleError, SecurityException, NoSuchMethodException {
		graph.add( OneClass.class );
		assertEquals( 0, graph.getExample( OneClass.class.getMethod( "testMethod" ) ).providers.size() );
		assertEquals( 1, graph.getExample( OneClass.class.getMethod( "anotherTestMethod" ) ).providers.size() );
		assertEquals( 1, graph.getExample( OneClass.class.getMethod( "depOnOtherTest" ) ).providers.size() );
		assertEquals( 0, graph.getExample( DependsParserTest.B.class.getMethod( "otherTest" ) ).providers.size() );
	}

	@Test
	public void detectCycles() {
        Result r = JExample.run( Cyclic.class );
        assertEquals( false, r.wasSuccessful() );
        assertEquals( 3, r.getRunCount() );
        assertEquals( 2, r.getFailureCount() );
        JExampleError err;
        err = (JExampleError) r.getFailures().get(0).getException();
        assertEquals( Kind.RECURSIVE_DEPENDENCIES, err.kind() );
        err = (JExampleError) r.getFailures().get(1).getException();
        assertEquals( Kind.RECURSIVE_DEPENDENCIES, err.kind() );
	}

	@RunWith(JExample.class)
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
		@Depends( "DependsParserTest$B.otherTest" )
		public void depOnOtherTest() {

		}
	}

    @RunWith(JExample.class)
	private static class Cyclic {
		@Test
		public void provider() { }
		@Test
		@Depends( "provider;aaa" )
		public void bbb() { }
		@Test
		@Depends( "bbb" )
		public void aaa() { }
	}

	private static class CyclicOverClasses {
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
