package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import jexample.Depends;
import jexample.JExample;
import jexample.internal.ExampleGraph;
import jexample.internal.InvalidExampleError;
import jexample.internal.InvalidExampleError.Kind;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.RunWith;


public class ExampleGraphTest {

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
		assertEquals( 0, graph.getExample( DependsParserTest.B.class.getMethod( "otherTest" ) ).providers.size() );
	}

	@Test
	public void detectCycles() throws InitializationError {
        try {
            graph.add( Cyclic.class );
            fail("InitializationError expected!");
        }
        catch (InitializationError ex) {
            assertEquals(1, ex.getCauses().size());
            assertEquals(InvalidExampleError.class, ex.getCauses().get(0).getClass());
            InvalidExampleError $ = (InvalidExampleError) ex.getCauses().get(0);
            assertEquals(Kind.RECURSIVE_DEPENDENCIES, $.kind);
        }
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
