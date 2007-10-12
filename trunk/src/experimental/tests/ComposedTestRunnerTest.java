/**
 * 
 */
package experimental.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import experimental.ComposedTestRunner;
import extension.annotations.Depends;
import extension.annotations.MyTest;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 * 
 */
public class ComposedTestRunnerTest {

	@RunWith(ComposedTestRunner.class)
	static public class BadTestMethods {

		public BadTestMethods() {
		}

		@MyTest
		public void noParametersAccepted(String bla) {
			assertTrue(false);
		}

		@MyTest
		public boolean returnTypeNotVoid() {
			assertTrue(false);
			return true;
		}

		@MyTest
		public void goodTest() {
			assertTrue(true);
		}
	}

	@Test
	@Ignore
	// the testmethods are not run, otherwise the test would fail
	public void badTestMethods() {
		Result result = JUnitCore.runClasses(BadTestMethods.class);
		// if there are initialization errors, here, there are two of them, then
		// for each
		// initialization error, a ErrorReportingRunner is created and only
		// those are run, so
		// there are only two Runs
		assertEquals(2, result.getRunCount());
		assertEquals(2, result.getFailureCount());
	}

	@RunWith(ComposedTestRunner.class)
	static public class CycleMethods {

		public CycleMethods() {
		}

		@MyTest
		@Depends("thirdMethod")
		public void firstMethod() {
			assertTrue(true);
		}

		@MyTest
		@Depends("firstMethod")
		public void secondMethod() {
			assertTrue(true);
		}

		@MyTest
		@Depends("secondMethod")
		public void thirdMethod() {
			assertTrue(true);
		}
	}

	@Test
	public void cycleMethods() {
		Result result = JUnitCore.runClasses(CycleMethods.class);
		assertEquals(1, result.getFailureCount());
		assertEquals("The dependencies are cyclic.", result.getFailures()
				.get(0).getMessage());
	}

	@RunWith(ComposedTestRunner.class)
	static public class SkipMethods {

		public SkipMethods() {
		}

		@MyTest
		public void firstMethod() {
			assertTrue(true);
		}

		// test is supposed to fail
		@MyTest
		@Depends("firstMethod")
		public void secondMethod() {
			assertTrue(false);
		}

		// this test is ignored, because secondMethod failed
		@MyTest
		@Depends("secondMethod")
		public void thirdMethod() {
			assertTrue(true);
		}

		// this test is ignored, because secondMethod failed
		@MyTest
		@Depends("secondMethod")
		public void fourthMethod() {
			assertTrue(true);
		}
	}

	@Test
	public void skipMethods() {
		Result result = JUnitCore.runClasses(SkipMethods.class);
		assertEquals(1, result.getFailureCount());
		assertEquals(2, result.getIgnoreCount());
		assertEquals(2, result.getRunCount());
	}

	@RunWith(ComposedTestRunner.class)
	static public class BadDependencies {

		public BadDependencies() {
		}

		public void firstMethod() {
			assertTrue(true);
		}

		@MyTest
		@Depends("firstMethod")
		public void secondMethod() {
			assertTrue(false);
		}

		@MyTest
		@Depends("secondMethod")
		public void thirdMethod() {
			assertTrue(true);
		}
	}

	@Test
	public void badDependencies() {
		Result result = JUnitCore.runClasses(BadDependencies.class);
		assertEquals(1, result.getFailureCount());
		assertEquals(1, result.getRunCount());
		assertEquals("Dependency firstMethod is not a test method.", result
				.getFailures().get(0).getMessage());
	}

	@RunWith(ComposedTestRunner.class)
	static public class GoodTest {
		public GoodTest() {
		}

		@MyTest
		public void firstMethod() {

		}

		@MyTest
		@Depends("firstMethod")
		public void secondMethod() {
			assertTrue(true);
		}

		@MyTest
		@Depends("secondMethod")
		public void thirdMethod() {
			assertTrue(true);
		}
	}

	@Test
	public void testGoodTest() {
		Result result = JUnitCore.runClasses(GoodTest.class);
		assertEquals(0, result.getFailureCount());
		assertEquals(0, result.getIgnoreCount());
		assertEquals(3, result.getRunCount());
	}

	@RunWith(ComposedTestRunner.class)
	static public class FirstGoodTest {
		public FirstGoodTest() {
		}

		@MyTest
		public void firstMethod() {

		}

		@MyTest
		@Depends("ComposedTestRunnerTest$SecondGoodTest.secondMethod")
		public void thirdMethod() {
			assertTrue(true);
		}
	}

	@RunWith(ComposedTestRunner.class)
	static public class SecondGoodTest {
		public SecondGoodTest() {
		}

		@MyTest
		@Depends("ComposedTestRunnerTest$FirstGoodTest.firstMethod")
		public void secondMethod() {
			assertTrue(true);
		}
	}

	@Test
	public void testGoodTests() {
		Result result = JUnitCore.runClasses(FirstGoodTest.class);
		assertEquals(0, result.getFailureCount());
		assertEquals(0, result.getIgnoreCount());
		assertEquals(3, result.getRunCount());
	}

	@RunWith(ComposedTestRunner.class)
	static public class FirstBadTest {
		public FirstBadTest() {
		}

		@MyTest
		public void firstMethod() {

		}

		@MyTest
		@Depends("ComposedTestRunnerTest$SecondBadTest.secondMethod")
		public void secondMethod() {
			assertTrue(true);
		}

		@MyTest
		@Depends("secondMethod")
		public void thirdMethod() {
			assertTrue(true);
		}
	}

	@RunWith(ComposedTestRunner.class)
	static public class SecondBadTest {
		public SecondBadTest() {
		}

		@MyTest
		@Depends("ComposedTestRunnerTest$FirstBadTest.secondMethod")
		public void secondMethod() {
			assertTrue(true);
		}
	}

	@Test
	public void testBadTests() {
		Result result = JUnitCore.runClasses(FirstBadTest.class);
		assertEquals(1, result.getFailureCount());
		assertEquals(0, result.getIgnoreCount());
		assertEquals(1, result.getRunCount());
	}

	@RunWith(ComposedTestRunner.class)
	static public class WithAttributes {
		public WithAttributes() {

		}

		@MyTest
		public String rootMethod() {
			return "Hello, I'm a string.";
		}
		
		@MyTest
		public int returnInteger() {
			return 2;
		}
		
		@MyTest
		public void noReturn(){
			
		}

		@MyTest
		@Depends("rootMethod")
		public String getsString(String aString) {
			assertEquals("Hello, I'm a string.", aString);
			return aString;
		}
		
		@MyTest
		@Depends("getsString(java.lang.String);returnInteger")
		public boolean getsStringAndInteger(String aString, int aInteger){
			assertEquals("Hello, I'm a string.", aString);
			assertEquals( 2, aInteger );
			return true;
		}
		
		@MyTest
		@Depends("getsStringAndInteger(java.lang.String,int)")
		public void findsDep(boolean aBool){
			assertTrue(aBool);
		}
	}

	@Test
	public void testWithAttributes() {
		Result result = JUnitCore.runClasses(WithAttributes.class);
		assertEquals(0, result.getFailureCount());
		assertEquals(0, result.getIgnoreCount());
		assertEquals(6, result.getRunCount());
	}
}
