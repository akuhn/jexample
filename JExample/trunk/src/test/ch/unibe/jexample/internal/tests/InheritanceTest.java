package ch.unibe.jexample.internal.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;
import ch.unibe.jexample.internal.Example;
import ch.unibe.jexample.internal.ExampleGraph;
import ch.unibe.jexample.internal.JExampleError;

public class InheritanceTest extends JExampleTest {

    @Before
    public void setup() {
        resetTrace();
    }
    
    @RunWith( JExample.class )
    public static class A {
        @Test
        public String m() {
            trace(this);
            return "A delivers";
        }
        @Test
        @Given("m()")
        public void test(String arg) {
            trace(this,arg);
        }
    }

    @RunWith( JExample.class )
    public static class B extends A {
        @Test
        @Override
        public String m() {
            trace(this);
            return "B delivers";
        }
    }
    
    @Test
    public void addB() throws JExampleError {
        ExampleGraph g = new ExampleGraph();
        g.add(B.class);
        
        assertEquals( 2, g.getExamples().size() );
        
        Example m = g.findExample(B.class, "m");
        Example test = g.findExample(B.class, "test");
        
        assertEquals( 1, test.providers.size() );
        assertEquals( m, test.providers.iterator().next() );
        
    }
    
    @Test
    public void addA() throws JExampleError {
        ExampleGraph g = new ExampleGraph();
        g.add(A.class);
        
        assertEquals( 2, g.getExamples().size() );
        
        Example m = g.findExample(A.class, "m");
        Example test = g.findExample(A.class, "test");
        
        assertEquals( 1, test.providers.size() );
        assertEquals( m, test.providers.iterator().next() );
        
    }

    @Test
    public void addAB() throws JExampleError {
        ExampleGraph g = new ExampleGraph();
        g.add(A.class);
        g.add(B.class);
        
        assertEquals( 4, g.getExamples().size() );
        
        Example m = g.findExample(A.class, "m");
        Example test = g.findExample(A.class, "test");
        
        assertEquals( 1, test.providers.size() );
        assertEquals( m, test.providers.iterator().next() );

        m = g.findExample(B.class, "m");
        test = g.findExample(B.class, "test");
        
        assertEquals( 1, test.providers.size() );
        assertEquals( m, test.providers.iterator().next() );        
    }
    
    @Test
    public void runA() throws JExampleError {
        ExampleGraph g = new ExampleGraph();
        assertTraceSize( 0 );
        g.runJExample(A.class);
        assertTraceSize( 2 );
        assertTrace( "A#m", "A#test" );
        assertTraceArgument( "A#test", "A delivers" );
    }

    @Test
    public void runB() throws JExampleError {
        ExampleGraph g = new ExampleGraph();
        assertTraceSize( 0 );
        g.runJExample(B.class);
        assertTraceSize( 2 );
        assertTrace( "B#m", "B#test" );
        assertTraceArgument( "B#test", "B delivers" );
    }

    @Test
    public void runAB() throws JExampleError {
        ExampleGraph g = new ExampleGraph();
        assertTraceSize( 0 );
        g.runJExample(A.class, B.class);
        assertTraceSize( 4 );
        assertTrace( "A#m", "A#test" );
        assertTraceArgument( "A#test", "A delivers" );
        assertTrace( "B#m", "B#test" );
        assertTraceArgument( "B#test", "B delivers" );
    }
    
}
