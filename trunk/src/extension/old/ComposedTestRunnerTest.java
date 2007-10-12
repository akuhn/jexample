/**
 * 
 */
package extension.old;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import extension.annotations.Depends;
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
	@Ignore
	// the testmethods are not run, otherwise the test would fail
	public void badTestMethods() {
		Result result = JUnitCore.runClasses( BadTestMethods.class );
		// if there are initialization errors, here, there are two of them, then for each
		// initialization error, a ErrorReportingRunner is created and only those are run, so
		// there are only two Runs
		assertEquals( 2, result.getRunCount() );
		assertEquals( 2, result.getFailureCount() );
	}
	
	@RunWith( ComposedTestRunner.class )
	static public class CycleMethods {

		public CycleMethods() {}

		@MyTest
		@Depends("thirdMethod")
		public void firstMethod( ) {
			assertTrue( true );
		}

		@MyTest
		@Depends("firstMethod")
		public void secondMethod() {
			assertTrue( true );
		}

		@MyTest
		@Depends("secondMethod")
		public void thirdMethod() {
			assertTrue( true );
		}
	}

	@Test
	public void cycleMethods() {
		Result result = JUnitCore.runClasses( CycleMethods.class );
		assertEquals( 1, result.getFailureCount() );
		assertEquals( "The dependencies are cyclic.", result.getFailures().get( 0 ).getMessage() );
	}
	
	@RunWith( ComposedTestRunner.class )
	static public class SkipMethods {

		public SkipMethods() {}

		@MyTest
		public void firstMethod( ) {
			assertTrue( true );
		}

		// test is supposed to fail
		@MyTest
		@Depends("firstMethod")
		public void secondMethod() {
			assertTrue( false );
		}

		// this test is ignored, because secondMethod failed
		@MyTest
		@Depends("secondMethod")
		public void thirdMethod() {
			assertTrue( true );
		}
	}
	
	@Test
	public void skipMethods() {
		Result result = JUnitCore.runClasses( SkipMethods.class );
		assertEquals( 1, result.getFailureCount() );
		assertEquals( 1, result.getIgnoreCount() );
		assertEquals( 2, result.getRunCount() );
	}
	
	@RunWith( ComposedTestRunner.class )
	static public class BadDependencies {

		public BadDependencies() {}

		public void firstMethod( ) {
			assertTrue( true );
		}

		@MyTest
		@Depends("firstMethod")
		public void secondMethod() {
			assertTrue( false );
		}

		@MyTest
		@Depends("secondMethod")
		public void thirdMethod() {
			assertTrue( true );
		}
	}
	
	@Test
	public void badDependencies() {
		Result result = JUnitCore.runClasses( BadDependencies.class );
		assertEquals( 1, result.getFailureCount() );
		assertEquals( 1, result.getRunCount() );
		assertEquals( "Dependency firstMethod is not a test method.", result.getFailures().get( 0 ).getMessage() );
	}
}
