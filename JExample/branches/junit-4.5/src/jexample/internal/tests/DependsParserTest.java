/**
 * 
 */
package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import jexample.Depends;
import jexample.JExample;
import jexample.internal.DependsParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @author Lea Haensenberger
 * @author Adrian Kuhn
 * 
 */
public class DependsParserTest {

    @RunWith( JExample.class )
    static class A {
        @Test public void unique() { }
        @Test public void test() { }
        @Test public void test(String s) { }
        @Test public void test(int x) { }
        @Test public void test(long x) { }
        @Test public void test(double x) { }
        @Test public void test(float x) { }
        @Test public void test(boolean x) { }
        @Test public void test(char x) { }
    }
    
	private DependsParser p;

	@Before
	public void setUp() throws Exception {
		p = new DependsParser( A.class );
	}

	@Test
	public void uniqueSimpleName() throws Exception {
	    Method[] $ = p.collectProviderMethods( "unique" );
	    assertEquals(1, $.length);
	    assertEquals("unique", $[0].getName());
	}
	
    @Test
    public void ambigousSimpleName() throws Exception {
        try {
            p.collectProviderMethods( "test" );
        }
        catch (NoSuchMethodException ex) {
            assertTrue(ex.getMessage().startsWith("Ambigous depedency"));
        }
    }
	
	
    @Test
    public void testWithoutParameters() throws Exception {
        Method[] $ = p.collectProviderMethods( "test()" );
        assertEquals(1, $.length);
        assertEquals("test", $[0].getName());
        assertEquals(0, $[0].getParameterTypes().length);
    }

    @Test
    public void testWithString() throws Exception {
        Method[] $ = p.collectProviderMethods( "test(String)" );
        assertEquals(1, $.length);
        assertEquals("test", $[0].getName());
        assertEquals(1, $[0].getParameterTypes().length);
        assertEquals(String.class, $[0].getParameterTypes()[0]);
    }

    @Test
    public void testWithInt() throws Exception {
        Method[] $ = p.collectProviderMethods( "test(int)" );
        assertEquals(1, $.length);
        assertEquals("test", $[0].getName());
        assertEquals(1, $[0].getParameterTypes().length);
        assertEquals(int.class, $[0].getParameterTypes()[0]);
    }
    
    @Test
    public void testWithLong() throws Exception {
        Method[] $ = p.collectProviderMethods( "test(long)" );
        assertEquals(1, $.length);
        assertEquals("test", $[0].getName());
        assertEquals(1, $[0].getParameterTypes().length);
        assertEquals(long.class, $[0].getParameterTypes()[0]);
    }
    
    @Test
    public void testWithFloat() throws Exception {
        Method[] $ = p.collectProviderMethods( "test(float)" );
        assertEquals(1, $.length);
        assertEquals("test", $[0].getName());
        assertEquals(1, $[0].getParameterTypes().length);
        assertEquals(float.class, $[0].getParameterTypes()[0]);
    }
    
    @Test
    public void testWithDouble() throws Exception {
        Method[] $ = p.collectProviderMethods( "test(double)" );
        assertEquals(1, $.length);
        assertEquals("test", $[0].getName());
        assertEquals(1, $[0].getParameterTypes().length);
        assertEquals(double.class, $[0].getParameterTypes()[0]);
    }
    
    @Test
    public void testWithChar() throws Exception {
        Method[] $ = p.collectProviderMethods( "test(char)" );
        assertEquals(1, $.length);
        assertEquals("test", $[0].getName());
        assertEquals(1, $[0].getParameterTypes().length);
        assertEquals(char.class, $[0].getParameterTypes()[0]);
    }    
    
    @Test
    public void testWithBoolean() throws Exception {
        Method[] $ = p.collectProviderMethods( "test(boolean)" );
        assertEquals(1, $.length);
        assertEquals("test", $[0].getName());
        assertEquals(1, $[0].getParameterTypes().length);
        assertEquals(boolean.class, $[0].getParameterTypes()[0]);
    }

	@Test( expected = ClassNotFoundException.class )
	public void testExtDepWithoutPackageNotFound() throws Exception {
		p.collectProviderMethods( "Zork.method" );
	}

    @Test
    public void packageLookup() throws Exception {
        p = new DependsParser( AllTests.class ); // same package
        Method[] $ = p.collectProviderMethods( "DependsParserTest$A.unique" );
        assertEquals(1, $.length);
        assertEquals("unique", $[0].getName());
    }

    @Test
    public void innerclassLookup() throws Exception {
        p = new DependsParser( DependsParserTest.class ); // same package
        Method[] $ = p.collectProviderMethods( "A.unique" );
        assertEquals(1, $.length);
        assertEquals("unique", $[0].getName());
    }
    
    
    @Test
    public void qualifiedLookup() throws Exception {
        p = new DependsParser( Void.class ); // totally different package 
        Method[] $ = p.collectProviderMethods(
                "jexample.internal.tests.DependsParserTest$A.unique");
        assertEquals(1, $.length);
        assertEquals("unique", $[0].getName());
    }
    
    @Test( expected = ClassNotFoundException.class )
    public void classNotFound() throws Exception {
        p.collectProviderMethods( "Zork.method" );
    }
    
    @Test( expected = NoSuchMethodException.class )
    public void noSuchMethod() throws Exception {
        p.collectProviderMethods( "zork()" );
    }
 
    @Test( expected = NoSuchMethodException.class )
    public void noSuchUniqueMethod() throws Exception {
        p.collectProviderMethods( "zork" );
    }
    
    @Test( expected = ClassNotFoundException.class )
    public void parameterTypeNotFound() throws Exception {
        p.collectProviderMethods( "test(zork)" );
    }
	
	
	@RunWith( JExample.class )
	static class B {

	    @Test public void otherTest() {

	    }

	    @Test
	    @Depends( "GraphTest$CyclicOverClasses.depOnOtherTest" )
	    public void otherTestCyclic() {

	    }

	    @Test
	    @Depends( "CycleDetectorTest$WithCycleOverClasses.bottomCyclicMethod" )
	    public void cyclicMethod() {

	    }

	    @Test
	    @Depends( "CycleDetectorTest$WithoutCycleOverClasses.rootMethod" )
	    public void middleMethod() {

	    }
	}	

}
