package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;

import jexample.Depends;
import jexample.JExample;
import jexample.JExampleOptions;
import jexample.internal.Example;
import jexample.internal.ExampleGraph;
import jexample.internal.JExampleError;
import jexample.internal.Util;

import org.junit.Test;
import org.junit.runner.RunWith;

public class ReturnValueTest {

    @RunWith(JExample.class)
    @JExampleOptions(cloneTestCase = true)
    private static class WithField {
        public String aString;
        
        @Test
        public void testChangeString(){
            this.aString = "Hello, World";
            assertEquals("Hello, World", this.aString);
        }
        
        @Test
        @Depends("#testChangeString")
        public void testField(){
            assertEquals("Hello, World", this.aString);
        }
    }

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
    
    @Test
    public void changeTestCaseField() throws Exception {
        Example e = runWithField("testChangeString");
        
        assertNull(e.returnValue.getValue());
        assertEquals( "Hello, World" , Util.getField(e.returnValue.getTestCaseInstance(), "aString") );
    }
    
    @Test
    public void fieldChanged() throws JExampleError{
        Example e = runWithField("testField");        
        assertTrue(e.wasSuccessful());
    }
    
    private Example runWithField(String example) throws JExampleError{
        ExampleGraph $ = new ExampleGraph();
        $.runJExample( WithField.class );
        Example e = $.findExample( WithField.class , example );
        return e;
    }
    
    
}
