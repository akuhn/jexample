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

		@MyTest
		public void goodTest() {
			assertTrue( true );
		}
	}

	@Test
	// the testmethods are not run, otherwise the test would fail
	public void badTestMethods() {
		Result result = JUnitCore.runClasses( BadTestMethods.class );
		// if there are initialization errors, here, there are two of them, then for each
		// initialization error, a ErrorReportingRunner is created and only those are run, so
		// there are only two Runs
		assertEquals( 2, result.getRunCount() );
		assertEquals( 2, result.getFailureCount() );
	}

}
