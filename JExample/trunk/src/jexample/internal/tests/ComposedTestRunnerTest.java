/**
 * 
 */
package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import jexample.Depends;
import jexample.JExampleRunner;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;


/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 * 
 */
public class ComposedTestRunnerTest {

	@RunWith( JExampleRunner.class )
	static public class CycleMethods {

		public CycleMethods() {
		}

		@Test
		@Depends( "thirdMethod" )
		public void firstMethod() {
			assertTrue( true );
		}

		@Test
		@Depends( "firstMethod" )
		public void secondMethod() {
			assertTrue( true );
		}

		@Test
		@Depends( "secondMethod" )
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

	@RunWith( JExampleRunner.class )
	static public class SkipMethods {

		public SkipMethods() {
		}

		@Test
		public void firstMethod() {
			assertTrue( true );
		}

		// test is supposed to fail
		@Test
		@Depends( "firstMethod" )
		public void secondMethod() {
			assertTrue( false );
		}

		// this test is ignored, because secondMethod failed
		@Test
		@Depends( "secondMethod" )
		public void thirdMethod() {
			assertTrue( true );
		}

		// this test is ignored, because secondMethod failed
		@Test
		@Depends( "secondMethod" )
		public void fourthMethod() {
			assertTrue( true );
		}
	}

	@Test
	public void skipMethods() {
		Result result = JUnitCore.runClasses( SkipMethods.class );
		assertEquals( 1, result.getFailureCount() );
		assertEquals( 2, result.getIgnoreCount() );
		assertEquals( 2, result.getRunCount() );
	}

	@RunWith( JExampleRunner.class )
	static public class BadDependencies {

		public BadDependencies() {
		}

		public void firstMethod() {
			assertTrue( true );
		}

		@Test
		@Depends( "firstMethod" )
		public void secondMethod() {
			assertTrue( false );
		}

		@Test
		@Depends( "secondMethod" )
		public void thirdMethod() {
			assertTrue( true );
		}
	}

	@Test
	@Ignore
	public void badDependencies() {
		Result result = JUnitCore.runClasses( BadDependencies.class );
		assertEquals( 2, result.getFailureCount() );
		assertEquals( 2, result.getRunCount() );
		assertEquals( "Dependency firstMethod is not a test method.", result.getFailures().get( 0 ).getMessage() );
	}

	@RunWith( JExampleRunner.class )
	static public class GoodTest {
		public GoodTest() {
		}

		@Test
		public void firstMethod() {

		}

		@Test
		@Depends( "firstMethod" )
		public void secondMethod() {
			assertTrue( true );
		}

		@Test
		@Depends( "secondMethod" )
		public void thirdMethod() {
			assertTrue( true );
		}
	}

	@Test
	public void testGoodTest() {
		Result result = JUnitCore.runClasses( GoodTest.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 3, result.getRunCount() );
	}

	@RunWith( JExampleRunner.class )
	static public class FirstGoodTest {
		public FirstGoodTest() {
		}

		@Test
		public void firstMethod() {

		}

		@Test
		@Depends( "ComposedTestRunnerTest$SecondGoodTest.secondMethod" )
		public void thirdMethod() {
			assertTrue( true );
		}
	}

	@RunWith( JExampleRunner.class )
	static public class SecondGoodTest {
		public SecondGoodTest() {
		}

		@Test
		@Depends( "ComposedTestRunnerTest$FirstGoodTest.firstMethod" )
		public void secondMethod() {
			assertTrue( true );
		}
	}

	@Test
	public void testGoodTests() {
		Result result = JUnitCore.runClasses( FirstGoodTest.class, SecondGoodTest.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 3, result.getRunCount() );
	}

	@RunWith( JExampleRunner.class )
	static public class FirstBadTest {
		public FirstBadTest() {
		}

		@Test
		public void firstMethod() {

		}

		@Test
		@Depends( "ComposedTestRunnerTest$SecondBadTest.secondMethod" )
		public void secondMethod() {
			assertTrue( true );
		}

		@Test
		@Depends( "secondMethod" )
		public void thirdMethod() {
			assertTrue( true );
		}
	}

	@RunWith( JExampleRunner.class )
	static public class SecondBadTest {
		public SecondBadTest() {
		}

		@Test
		@Depends( "ComposedTestRunnerTest$FirstBadTest.secondMethod" )
		public void secondMethod() {
			assertTrue( true );
		}
	}

	@Test
	public void testBadTests() {
		Result result = JUnitCore.runClasses( FirstBadTest.class );
		assertEquals( 1, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 1, result.getRunCount() );
	}

	@RunWith( JExampleRunner.class )
	static public class WithAttributes {
		public WithAttributes() {

		}

		@Test
		public String rootMethod() {
			return "Hello, I'm a string.";
		}

		@Test
		public int returnInteger() {
			return 2;
		}

		@Test
		public void noReturn() {

		}

		@Test
		@Depends( "rootMethod" )
		public String getsString( String aString ) {
			assertEquals( "Hello, I'm a string.", aString );
			return aString;
		}

		@Test
		@Depends( "getsString(java.lang.String);returnInteger" )
		public boolean getsStringAndInteger( String aString, int aInteger ) {
			assertEquals( "Hello, I'm a string.", aString );
			assertEquals( 2, aInteger );
			return true;
		}

		@Test
		@Depends( "getsStringAndInteger(java.lang.String,int)" )
		public void findsDep( boolean aBool ) {
			assertTrue( aBool );
		}
	}

	@Test
	public void testWithAttributes() {
		Result result = JUnitCore.runClasses( WithAttributes.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 6, result.getRunCount() );
	}

	@RunWith( JExampleRunner.class )
	static public class DependsOnBeforeTest {

		public DependsOnBeforeTest() {
		}

		@Test
		public int root() {
			return 2;
		}

		@Test
		@Depends( "root" )
		public String second( int i ) {
			assertEquals( 2, i );
			return "bla";
		}

		@Test
		@Depends( "second(int)" )
		public void third( String aString ) {
			assertEquals( "bla", aString );
		}
	}

	@Test
	public void testDependsOnBefore() {
		Result result = JUnitCore.runClasses( DependsOnBeforeTest.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 3, result.getRunCount() );
	}

	@RunWith( JExampleRunner.class )
	static public class CloneRetVal {

		public CloneRetVal() {
		}

		@Test
		public Clone root() {
			return new Clone( "original" );
		}

		@Test
		@Depends( "root" )
		public void second( Clone aClone ) {
			assertEquals( "clone", aClone.getName() );
		}

		static public class Clone implements Cloneable {
			private final String name;

			public Clone() {
				this.name = "";
			}

			public Clone( String name ) {
				this.name = name;
			}

			public Object clone() {
				return new Clone( "clone" );
			}

			public String getName() {
				return this.name;
			}
		}
	}

	@Test
	public void testCloneRetVal() {
		Result result = JUnitCore.runClasses( CloneRetVal.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 2, result.getRunCount() );
	}
	
	@RunWith( JExampleRunner.class )
	static public class NotCloneRetVal {

		private static NoClone rootClone, secondClone;
		
		public NotCloneRetVal() {
		}

		@Test
		public NoClone root() {
			NoClone clone = new NoClone("original");
			NotCloneRetVal.rootClone = clone;
			return clone;
		}

		@Test
		@Depends( "root" )
		public NoClone second( NoClone aClone ) {
			NotCloneRetVal.secondClone = aClone;
			assertEquals( "original", aClone.getName() );
			assertSame(NotCloneRetVal.rootClone, aClone);
			
			return aClone;
		}
		
		@Test
		@Depends( "root" )
		public void third( NoClone aClone ) {
			assertEquals( "original", aClone.getName() );
			assertSame(NotCloneRetVal.rootClone, aClone);
			assertNotSame(NotCloneRetVal.secondClone,aClone);
		}
		
		@Test
		@Depends("second(jexample.internal.tests.ComposedTestRunnerTest$NotCloneRetVal$NoClone)")
		public void fourth(NoClone aClone){
			assertEquals( "original", aClone.getName() );
			assertSame(NotCloneRetVal.rootClone, aClone);
			assertSame(NotCloneRetVal.secondClone,aClone);
		}

		static public class NoClone {
			private final String name;

			public NoClone() {
				this.name = "";
			}

			public NoClone( String name ) {
				this.name = name;
			}

			public String getName() {
				return this.name;
			}
		}
	}

	@Test
	public void testNotCloneRetVal() {
		Result result = JUnitCore.runClasses( NotCloneRetVal.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 4, result.getRunCount() );
	}
}
