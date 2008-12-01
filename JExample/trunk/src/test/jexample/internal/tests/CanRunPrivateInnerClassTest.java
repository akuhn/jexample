package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import jexample.JExample;
import jexample.internal.ExampleGraph;
import jexample.internal.JExampleError;

import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

public class CanRunPrivateInnerClassTest {

    @RunWith( JExample.class )
    static class PrivateInnerClass {
        @Test public void success() { }
    }
    
    @Test
    public void createJExampleRunner() throws JExampleError {
        new JExample( new ExampleGraph().add( PrivateInnerClass.class ));
    }
    
    @Test
    public void testRunningPrivateInnerClass() throws JExampleError  {
    	Result result = Util.runAllExamples(PrivateInnerClass.class);
        assertEquals(true, result.wasSuccessful());
        assertEquals(1, result.getRunCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());
    }
    
}
