package experimental.tests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;

import experimental.TestGraph;
import experimental.TestClass;
import experimental.TestMethod;
import extension.annotations.Depends;
import extension.annotations.MyTest;

public class GraphTest {

	private static TestGraph graph;

	@Before
	public void setUp() throws Exception {
		graph = TestGraph.getInstance();
	}

	@Test
	public void testAddOneClass() throws InitializationError {
		graph.addClass( new TestClass(OneClass.class) );
		assertEquals( 1, graph.getClasses().size() );
		assertEquals( 4, graph.getTestMethods().size() );
	}

	@Test
	public void testAddMethodsOfOneClass() throws InitializationError {
		graph.addClass( new TestClass(OneClass.class) );
		assertEquals( 4, graph.getTestMethods().size() );
	}

	@Test
	public void testAddDependenciesOfOneClass() throws InitializationError, SecurityException, NoSuchMethodException {
		graph.addClass( new TestClass(OneClass.class) );
		Map<Method, TestMethod> testMethods = graph.getTestMethods();
		assertEquals( 0, testMethods.get( OneClass.class.getMethod( "testMethod" ) ).getDependencies().size() );
		assertEquals( 1, testMethods.get( OneClass.class.getMethod( "anotherTestMethod" ) ).getDependencies().size() );
		assertEquals( 1, testMethods.get( OneClass.class.getMethod( "depOnOtherTest" ) ).getDependencies().size() );
		assertEquals( 0, testMethods.get( B.class.getMethod( "otherTest" ) ).getDependencies().size() );
	}
	
	@Test(expected=InitializationError.class)
	public void detectCycles() throws InitializationError{
		graph.addClass( new TestClass(Cyclic.class) );
	}

	static public class OneClass {
		
		public OneClass() {
			
		}
		
		@MyTest
		public void testMethod() {

		}

		@MyTest
		@Depends( "testMethod" )
		public void anotherTestMethod() {

		}

		@MyTest
		@Depends( "B.otherTest" )
		public void depOnOtherTest() {

		}
	}
	
	static public class Cyclic {
		public Cyclic() {
			
		}
		
		@MyTest
		public void testMethod() {

		}

		@MyTest
		@Depends( "testMethod;depOnOtherTest" )
		public void anotherTestMethod() {

		}

		@MyTest
		@Depends( "anotherTestMethod" )
		public void depOnOtherTest() {

		}
	}
	
	static public class CyclicOverClasses {
		public CyclicOverClasses() {
			
		}
		
		@MyTest
		public void testMethod() {

		}

		@MyTest
		@Depends( "testMethod" )
		public void anotherTestMethod() {

		}

		@MyTest
		@Depends( "B.otherTestCyclic" )
		public void depOnOtherTest() {

		}
	}
}
