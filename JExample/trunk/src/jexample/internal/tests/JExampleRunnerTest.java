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
import jexample.internal.Example;
import jexample.internal.ExampleGraph;
import jexample.internal.InvalidExampleError;
import jexample.internal.InvalidExampleError.Kind;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;


/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 * 
 */
public class JExampleRunnerTest {

	@RunWith( JExampleRunner.class )
	private static class CycleMethods {

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
		Result result = runJExampleTestCase( CycleMethods.class );
		assertEquals( 1, result.getFailureCount() );
		InvalidExampleError $ = (InvalidExampleError) result.getFailures().get(0).getException();
		assertEquals(Kind.RECURSIVE_DEPENDENCIES, $.kind);
	}

	@RunWith( JExampleRunner.class )
	private static class SkipMethods {

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
		Result result = runJExampleTestCase( SkipMethods.class );
		assertEquals( 1, result.getFailureCount() );
		assertEquals( 2, result.getIgnoreCount() );
		assertEquals( 2, result.getRunCount() );
	}

	@RunWith( JExampleRunner.class )
	private static class BadDependencies {

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
	public void badDependencies() {
		Result result = runJExampleTestCase( BadDependencies.class );
		assertEquals( 1, result.getFailureCount() );
		assertEquals( 1, result.getRunCount() );
		//assertEquals( "Dependency firstMethod is not a test method.", result.getFailures().get( 0 ).getMessage() );
	}

	private Result runJExampleTestCase(Class<?>... classes) {
        return new JUnitCore().run(new ExampleGraph().newJExampleRunner(classes));
    }

    @RunWith( JExampleRunner.class )
	private static class GoodTest {
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
	    Result result = runJExampleTestCase(GoodTest.class);
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 3, result.getRunCount() );
	}

	@RunWith( JExampleRunner.class )
	private static class FirstGoodTest {
		public FirstGoodTest() {
		}

		@Test
		public void firstMethod() {

		}

		@Test
		@Depends( "JExampleRunnerTest$SecondGoodTest.secondMethod" )
		public void thirdMethod() {
			assertTrue( true );
		}
	}

	@RunWith( JExampleRunner.class )
	private static class SecondGoodTest {
		public SecondGoodTest() {
		}

		@Test
		@Depends( "JExampleRunnerTest$FirstGoodTest.firstMethod" )
		public void secondMethod() {
			assertTrue( true );
		}
	}

	@Test
	public void testGoodTests() {
		Result result = runJExampleTestCase( FirstGoodTest.class, SecondGoodTest.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 3, result.getRunCount() );
	}

	@RunWith( JExampleRunner.class )
	private static class FirstBadTest {
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
	private static class SecondBadTest {
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
		Result result = runJExampleTestCase( FirstBadTest.class );
		assertEquals( 1, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 1, result.getRunCount() );
	}

	@RunWith( JExampleRunner.class )
	private static class WithAttributes {
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
		Result result = runJExampleTestCase( WithAttributes.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 6, result.getRunCount() );
	}

	@RunWith( JExampleRunner.class )
	private static class DependsOnBeforeTest {

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
		Result result = runJExampleTestCase( DependsOnBeforeTest.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 3, result.getRunCount() );
	}

	@RunWith( JExampleRunner.class )
	private static class CloneRetVal {

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

			@Override
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
		Result result = runJExampleTestCase( CloneRetVal.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 2, result.getRunCount() );
	}
	
	@RunWith( JExampleRunner.class )
	private static class NotCloneRetVal {

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
		@Depends("second")
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
		Result result = runJExampleTestCase( NotCloneRetVal.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 4, result.getRunCount() );
	}
	
	@Test
	public void filter() throws NoTestsRemainException {
	    ExampleGraph g = new ExampleGraph();
	    Runner r = g.newJExampleRunner( StackTest.class );
	    Example e = g.findExample( StackTest.class, "withValue" );
	    ((Filterable) r).filter(newFilter(e.description));
	    Result $ = new JUnitCore().run(r);
	    assertEquals(2, $.getRunCount()); 
        assertEquals(0, $.getIgnoreCount()); // it says filter, not ignore!
        assertEquals(0, $.getFailureCount());
        assertEquals(true, $.wasSuccessful()); 
	}
	
	
	private Filter newFilter(final Description d) {
	    return new Filter() {
	        @Override
	        public String describe() {
	            return String.format("Method %s", d);
	        }
	        @Override
	        public boolean shouldRun(Description description) {
                if (d.isTest())
                    return d.equals(description);
                for (Description each : d.getChildren())
                    if (shouldRun(each))
                        return true;
                return false;   	        
            }
	    };
	}
	
	@RunWith( JExampleRunner.class )
	static class A_fail {
	    @Test( expected = Exception.class )
	    public void fail() {
	        throw new Error();
	    }
	}
	
	@Test
	public void unexpectedException() {
	    Result $ = runJExampleTestCase( A_fail.class );
	    assertEquals(1, $.getRunCount());
	    assertEquals(false, $.wasSuccessful());
	    assertEquals(1, $.getFailureCount());
	    assertTrue($.getFailures().get(0).getMessage()
	            .startsWith("Unexpected exception, expected"));
	}
}
