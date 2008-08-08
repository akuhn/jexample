/**
 * 
 */
package jexample.internal.tests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.List;

import jexample.Depends;
import jexample.JExampleRunner;
import jexample.internal.DependsParser;
import jexample.internal.TestClass;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class DependsParserTest {

	private DependsParser parser;
	private TestClass myClass;
	private Method annotatedMethod;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		parser = new DependsParser( TestTestClass.class );
		annotatedMethod = TestTestClass.class.getMethod( "annotatedMethod" );
	}

	@Test
	public void testGetDependencies() throws SecurityException, ClassNotFoundException, NoSuchMethodException {
		List<Method> methods = this.parser.getDependencies( "annotatedMethod()" );
		assertEquals( 1, methods.size() ); 

		methods = this.parser.getDependencies( "annotatedMethod(java.lang.String)" );
		assertEquals( 1, methods.size() );

		methods = this.parser.getDependencies( "annotatedMethod();annotatedMethod(java.lang.String)" );
		assertEquals( 2, methods.size() );

		methods = this.parser.getDependencies( "annotatedMethod(int)" );
		assertEquals( 1, methods.size() );
	}

	@Test
	public void testExternalDeps() throws SecurityException, ClassNotFoundException, NoSuchMethodException {
		List<Method> methods = this.parser.getDependencies( "DependsParserTest$B.otherTest" );
		assertEquals( 1, methods.size() );
	}

	@Test( expected = ClassNotFoundException.class )
	public void testExtDepWithoutPackageNotFound() throws SecurityException, ClassNotFoundException,
			NoSuchMethodException {
		List<Method> methods = this.parser.getDependencies( "TestClass.getJavaClass" );
		assertEquals( 1, methods.size() );
	}

	@Test
	public void testExtDepWithPackageFound() throws SecurityException, ClassNotFoundException, NoSuchMethodException {
		List<Method> methods = this.parser.getDependencies( "jexample.internal.TestClass.getJavaClass" );
		assertEquals( 1, methods.size() );
	}

	@RunWith( JExampleRunner.class )
	public static class TestTestClass {
		@Test
		public void annotatedMethod() {

		}

		@Test
		public void annotatedMethod( String string ) {

		}

		@Test
		public void annotatedMethod( int i ) {

		}
	}
	
	@RunWith( JExampleRunner.class )
	public static class B {

	    public B() {

	    }

	    @Test
	    public void otherTest() {

	    }

	    @Test
	    @Depends( "GraphTest$CyclicOverClasses.depOnOtherTest" )
	    public void otherTestCyclic() {

	    }

	    @Test
	    @Depends( "CycleDetectorTest$WithCycleOverClasses.bottomCyclicMethod" )
	    public void cyclicMethod() {

	    }

	    @Test
	    @Depends( "CycleDetectorTest$WithoutCycleOverClasses.rootMethod" )
	    public void middleMethod() {

	    }
	}	

}
