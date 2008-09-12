package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import jexample.JExample;
import jexample.internal.Example;
import jexample.internal.ExampleGraph;
import jexample.internal.JExampleError;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;

public class ReturnValueTest {

    @RunWith( JExample.class )
    private static class Null {
        @Test
        public Object returnNull() {
            return null;
        }
    }
    
    @Test
    public void returnValueIsNull() throws JExampleError {
        Example e = runNullExample();
        
        assertTrue(e.wasSuccessful());
        assertEquals(null, e.returnValue.getValue());
    }

    private Example runNullExample() throws JExampleError {
        ExampleGraph $ = new ExampleGraph();
        $.runJExample( Null.class );
        Example e = $.findExample( Null.class , "returnNull" );
        return e;
    }

    @Test
    public void nullIsCloneable() throws JExampleError {
        Example e = runNullExample();
        
        assertEquals(null, e.returnValue.getValue());
        assertTrue(e.returnValue.isCloneable());
    }
    
}
