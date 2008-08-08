/**
 * 
 */
package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import jexample.Depends;
import jexample.JExample;
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

import demo.StackTest;


/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 * 
 */
public class JExampleRunnerTest {

	@RunWith( JExample.class )
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
		Result result = JExample.run( CycleMethods.class );
		assertEquals( 1, result.getFailureCount() );
		InvalidExampleError $ = (InvalidExampleError) result.getFailures().get(0).getException();
		assertEquals(Kind.RECURSIVE_DEPENDENCIES, $.kind);
	}

	@RunWith( JExample.class )
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
		Result result = JExample.run( SkipMethods.class );
		assertEquals( 1, result.getFailureCount() );
		assertEquals( 2, result.getIgnoreCount() );
		assertEquals( 2, result.getRunCount() );
	}

	@RunWith( JExample.class )
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
	    Result result = JExample.run(GoodTest.class);
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 3, result.getRunCount() );
	}

	@RunWith( JExample.class )
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

	@RunWith( JExample.class )
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
		Result result = JExample.run( FirstGoodTest.class, SecondGoodTest.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 3, result.getRunCount() );
	}



	@RunWith( JExample.class )
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
		Result result = JExample.run( WithAttributes.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 6, result.getRunCount() );
	}

	@RunWith( JExample.class )
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
		Result result = JExample.run( DependsOnBeforeTest.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 3, result.getRunCount() );
	}

	@RunWith( JExample.class )
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
		Result result = JExample.run( CloneRetVal.class );
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 2, result.getRunCount() );
	}
	
	@RunWith( JExample.class )
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
		Result result = JExample.run( NotCloneRetVal.class );
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
	
	@RunWith( JExample.class )
	static class A_fail {
	    @Test( expected = Exception.class )
	    public void fail() {
	        throw new Error();
	    }
	}
	
	@Test
	public void unexpectedException() {
	    Result $ = JExample.run( A_fail.class );
	    assertEquals(1, $.getRunCount());
	    assertEquals(false, $.wasSuccessful());
	    assertEquals(1, $.getFailureCount());
	    assertTrue($.getFailures().get(0).getMessage()
	            .startsWith("Unexpected exception, expected"));
	}
	
    @RunWith( JExample.class )
    static class B_fail {
        public void missingAnnotation() { }
        @Test
        @Depends("#missingAnnotation")
        public void provider() { }
        @Test
        @Depends("#provider" )
        public void consumer() { }
    }

    @Test
    public void dependsOnNonTestMethodFails() {
        Result result = JExample.run( B_fail.class );
        assertEquals( false, result.wasSuccessful() );
        assertEquals( 2, result.getRunCount() );
        assertEquals( 2, result.getFailureCount() );
    }

    @RunWith( JExample.class )
    static class C_fail {
        @Test
        @Depends( "D#test" )
        public void test() {
            assertTrue( true );
        }
    }

    @RunWith( JExample.class )
    static class D_fail {
        @Test
        @Depends( "C#test" )
        public void test() { }
    }

    @Test
    public void testBadTests() {
        Result result = JExample.run( D_fail.class, C_fail.class );
        assertEquals( 2, result.getFailureCount() );
        assertEquals( 0, result.getIgnoreCount() );
        assertEquals( 2, result.getRunCount() );
    }
    
	
}
