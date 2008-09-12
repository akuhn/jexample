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
import jexample.demo.StackTest;
import jexample.internal.Example;
import jexample.internal.ExampleClass;
import jexample.internal.ExampleGraph;
import jexample.internal.JExampleError;
import jexample.internal.JExampleError.Kind;

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

	@RunWith( JExample.class )
	private static class CycleOfThree {
		@Test
		@Depends( "ccc" )
		public void aaa() { }
		@Test
		@Depends( "aaa" )
		public void bbb()  { }
		@Test
		@Depends( "bbb" )
		public void ccc() { }
	}

	@Test
	public void cycleMethods() throws JExampleError {
		Class<?>[] classes = { CycleOfThree.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
        assertEquals( false, result.wasSuccessful() );
        assertEquals( 3, result.getRunCount() );
		assertEquals( 3, result.getFailureCount() );
		JExampleError err = (JExampleError) result.getFailures().get(0).getException();
        assertEquals(1, err.size());
        assertEquals(Kind.RECURSIVE_DEPENDENCIES, err.kind());
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
	public void skipMethods() throws JExampleError {
		Class<?>[] classes = { SkipMethods.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
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
	public void testGoodTest() throws JExampleError {
	    Class<?>[] classes = { GoodTest.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
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
	public void testGoodTests() throws JExampleError {
		Class<?>[] classes = { FirstGoodTest.class, SecondGoodTest.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
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
	public void testWithAttributes() throws JExampleError {
		Class<?>[] classes = { WithAttributes.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
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
	public void testDependsOnBefore() throws JExampleError {
		Class<?>[] classes = { DependsOnBeforeTest.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
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
	public void testCloneRetVal() throws JExampleError {
		Class<?>[] classes = { CloneRetVal.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
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
	public void testNotCloneRetVal() throws JExampleError {
		Class<?>[] classes = { NotCloneRetVal.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
		assertEquals( 0, result.getFailureCount() );
		assertEquals( 0, result.getIgnoreCount() );
		assertEquals( 4, result.getRunCount() );
	}
	
	@Test
	public void filter() throws NoTestsRemainException, JExampleError {
	    ExampleGraph g = new ExampleGraph();
	    Runner r = new JExample( g.add(StackTest.class) );
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
	public void unexpectedException() throws JExampleError {
	    Class<?>[] classes = { A_fail.class };
        ExampleGraph g = new ExampleGraph();
        Result $ = g.runJExample(classes);
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
    public void dependsOnNonTestMethodFails() throws JExampleError {
        Class<?>[] classes = { B_fail.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
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
    public void testBadTests() throws JExampleError {
        Class<?>[] classes = { D_fail.class, C_fail.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
        assertEquals( 2, result.getFailureCount() );
        assertEquals( 0, result.getIgnoreCount() );
        assertEquals( 2, result.getRunCount() );
    }
    
    @Test
    public void exampleClassesAreUnique() throws JExampleError {
        ExampleGraph g = new ExampleGraph();
        ExampleClass aaa = g.add( StackTest.class );
        ExampleClass bbb = g.add( StackTest.class );
        
        assertSame(aaa, bbb);
    }
    
	
}
