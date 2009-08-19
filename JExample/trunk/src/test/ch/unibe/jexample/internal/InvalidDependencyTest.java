package ch.unibe.jexample.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;
import ch.unibe.jexample.util.JExampleError;

public class InvalidDependencyTest {

    @RunWith(JExample.class)
    public static class A {
        @Given("^^") public void broken() { assert false; }
    }
    
    @Test
    public void shouldFailsWithInvalidDependency() throws JExampleError {
       Result result = new ExampleGraph().runJExample(A.class); 
       assertEquals(1, result.getRunCount());
       assertEquals(1, result.getFailureCount());
       Failure failure = result.getFailures().iterator().next();
       assertEquals("broken(" + A.class.getName() + ")", failure.getTestHeader());
    }
    
    
}
