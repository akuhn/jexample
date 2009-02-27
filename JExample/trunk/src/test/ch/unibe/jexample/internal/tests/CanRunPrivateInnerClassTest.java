package ch.unibe.jexample.internal.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import ch.unibe.jexample.JExample;
import ch.unibe.jexample.internal.ExampleGraph;
import ch.unibe.jexample.internal.JExampleError;

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
