/**
 * 
 */
package extension.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import extension.ComposedTestRunner;
import extension.annotations.MyTest;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 * 
 */
public class ComposedTestRunnerTest {

	@RunWith( ComposedTestRunner.class )
	static public class BadTestMethods {

		public BadTestMethods() {}

		@MyTest
		public void noParametersAccepted( String bla ) {
			assertTrue( false );
		}

		@MyTest
		public boolean returnTypeNotVoid() {
			assertTrue( false );
			return true;
		}
	}

	@Test
	// the testmethods are not run, otherwise the test would fail
	public void badTestMethods() {
		Result result = JUnitCore.runClasses( BadTestMethods.class );
		assertEquals( 1, result.getRunCount() );
		// i don't exactly know, why it's 1 failure, but i think it counts just
		// the initializationerror
		assertEquals( 1, result.getFailureCount() );
	}

}
