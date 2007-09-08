/**
 * 
 */
package extension.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;

import extension.ComposedTestRunner;
import extension.annotations.MyTest;

/**
 * @author Lea Haensenberger 
 * Date: Sep 7, 2007
 */
@RunWith( ComposedTestRunner.class )
public class TestTest {

	public TestTest() {

	}

	@MyTest
	public void testTestMethod() {
		System.out.println( "aTest" );
		assertTrue( true );
	}

	@MyTest
	public void anotherTestMethod() {
		System.out.println( "anotherTest" );
		assertFalse( false );
	}

	public void noTestMethod() {
		System.out.println( "noTest" );
		assertTrue( false );
	}
}
