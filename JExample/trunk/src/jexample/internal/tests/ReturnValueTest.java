package jexample.internal.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import jexample.JExampleRunner;
import jexample.internal.Example;
import jexample.internal.ExampleGraph;
import jexample.internal.ReturnValue;
import jexample.internal.tests.InjectionPolicyTest.B;

public class ReturnValueTest {

    @RunWith( JExampleRunner.class )
    public static class Null {
        @Test
        public Object returnNull() {
            return null;
        }
    }
    
    @Test
    public void returnValueIsNull() {
        Example e = runNullExample();
        
        assertTrue(e.wasSuccessful());
        assertEquals(null, e.returnValue.getValue());
    }

    private Example runNullExample() {
        ExampleGraph $ = new ExampleGraph();
        new JUnitCore().run($.newJExampleRunner( Null.class ));
        Example e = $.findExample( Null.class , "returnNull" );
        return e;
    }

    @Test
    public void nullIsCloneable() {
        Example e = runNullExample();
        
        assertEquals(null, e.returnValue.getValue());
        assertTrue(e.returnValue.isCloneable());
    }
    
}
