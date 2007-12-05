/**
 * 
 */
package extension.tests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import extension.ComposedTestRunner;
import extension.DependencyParser;
import extension.TestClass;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class DependencyParserTest {

	private DependencyParser parser;
	private TestClass myClass;
	private Method annotatedMethod;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.myClass = new TestClass( this.getClass() );
		parser = new DependencyParser( myClass );
		annotatedMethod = TestTestClass.class.getMethod( "annotatedMethod" );
	}

	@Test
	public void testGetDependencies() throws SecurityException, ClassNotFoundException, NoSuchMethodException {
		List<Method> methods = this.parser.getDependencies( "annotatedMethod", annotatedMethod );
		assertEquals( 1, methods.size() );

		methods = this.parser.getDependencies( "annotatedMethod(java.lang.String)", annotatedMethod );
		assertEquals( 1, methods.size() );

		methods = this.parser.getDependencies( "annotatedMethod;annotatedMethod(java.lang.String)", annotatedMethod );
		assertEquals( 2, methods.size() );

		methods = this.parser.getDependencies( "annotatedMethod(int)", annotatedMethod );
		assertEquals( 1, methods.size() );
	}

	@Test
	public void testExternalDeps() throws SecurityException, ClassNotFoundException, NoSuchMethodException {
		List<Method> methods = this.parser.getDependencies( "B.otherTest", annotatedMethod );
		assertEquals( 1, methods.size() );
	}

	@Test( expected = ClassNotFoundException.class )
	public void testExtDepWithoutPackageNotFound() throws SecurityException, ClassNotFoundException,
			NoSuchMethodException {
		List<Method> methods = this.parser.getDependencies( "TestClass.getJavaClass", annotatedMethod );
		assertEquals( 1, methods.size() );
	}

	@Test
	public void testExtDepWithPackageFound() throws SecurityException, ClassNotFoundException, NoSuchMethodException {
		List<Method> methods = this.parser.getDependencies( "extension.TestClass.getJavaClass", annotatedMethod );
		assertEquals( 1, methods.size() );
	}

	@RunWith( ComposedTestRunner.class )
	private class TestTestClass {
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

}
