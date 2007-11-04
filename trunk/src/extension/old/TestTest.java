/**
 * 
 */
package extension.old;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;

import extension.annotations.Depends;
import extension.annotations.MyTest;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 * 
 */
@RunWith( ComposedTestRunner.class )
public class TestTest {

	public TestTest() {

	}

	@MyTest
	@Depends("anotherTestMethod")
	public void testTestMethod() {
		System.out.println( "aTest" );
		assertTrue( true );
	}

	@MyTest
	public void anotherTestMethod() {
		System.out.println( "anotherTest" );
		assertFalse( false );
	}
	
	@MyTest
	@Depends("testTestMethod")
	public void lastMethod(){
		System.out.println( "lastTest" );
		assertTrue(true);
	}

	public void noTestMethod() {
		System.out.println( "noTest" );
		assertTrue( false );
	}
}