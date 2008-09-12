package jexample.internal.tests;

import static org.junit.Assert.*;
import jexample.Depends;
import jexample.JExample;
import jexample.internal.Example;
import jexample.internal.ExampleGraph;
import jexample.internal.JExampleError;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

public class InheritanceTest {

    @Before
    public void setup() {
        Trace.reset();
    }
    
    @RunWith( JExample.class )
    public static class A {
        @Test
        public String m() {
            Trace.record();
            return "A";
        }
        @Test
        @Depends("m()")
        public void test(String arg) {
            Trace.record(arg);
        }
    }

    @RunWith( JExample.class )
    public static class B {
        @Test
        public String m() {
            Trace.record();
            return "B";
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
    
}
