package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import jexample.JExample;
import jexample.internal.ExampleGraph;

import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

public class CanRunPrivateInnerClassTest {

    @RunWith( JExample.class )
    static class PrivateInnerClass {
        @Test public void success() { }
    }
    
    @Test
    public void createJExampleRunner() {
        new ExampleGraph().newJExampleRunner( PrivateInnerClass.class );
    }
    
    @Test
    public void testRunningPrivateInnerClass()  {
        Result result = JExample.run( PrivateInnerClass.class );
        assertEquals(true, result.wasSuccessful());
        assertEquals(1, result.getRunCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());
    }
    
}
