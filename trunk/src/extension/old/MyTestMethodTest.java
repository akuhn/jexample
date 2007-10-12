/**
 * 
 */
package extension.old;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import extension.annotations.Depends;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class MyTestMethodTest {

	private MyTestMethod testMethod;
	private MyTestClass testClass;
	private MyTestMethod testMethod2;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testClass = new MyTestClass( this.getClass() );
		testMethod = new MyTestMethod( this.getClass().getMethod( "testMethod" ), testClass );
		testMethod2 = new MyTestMethod( this.getClass().getMethod( "testMethod2" ), testClass );
	}
	
	@Test
	public void testGetDependencies() throws SecurityException, ClassNotFoundException, NoSuchMethodException{
		assertEquals(1, this.testMethod.getDependencies().size());
		assertEquals(0, this.testMethod2.getDependencies().size());
	}

	@Depends( "testMethod2" )
	public void testMethod() {

	}

	public void testMethod2() {

	}
}
