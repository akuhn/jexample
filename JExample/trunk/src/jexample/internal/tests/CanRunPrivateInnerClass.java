package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import jexample.JExampleRunner;
import jexample.internal.ExampleGraph;

import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

public class CanRunPrivateInnerClass {

    @RunWith( JExampleRunner.class )
    private static class PrivateInnerClass {
        @Test public void success() { }
    }
    
    @Test
    public void createJExampleRunner() {
        new ExampleGraph().newJExampleRunner( PrivateInnerClass.class );
    }
    
    @Test
    public void testRunningPrivateInnerClass()  {
        Result result = new JUnitCore().run(new ExampleGraph().newJExampleRunner( PrivateInnerClass.class ));;
        assertEquals(true, result.wasSuccessful());
        assertEquals(1, result.getRunCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());
    }
    
}
