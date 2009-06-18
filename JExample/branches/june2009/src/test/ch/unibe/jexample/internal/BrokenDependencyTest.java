package ch.unibe.jexample.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;
import ch.unibe.jexample.util.JExampleError;

public class BrokenDependencyTest {

    @RunWith(JExample.class)
    static class A {
        @Test public int m() { return 1; }
        @Test public int n() { return 2;}
        @Test 
        @Given("#m,#a,#n")
        public Object t(int m, int a, int n) { 
            throw new RuntimeException("Not reachable!");
        }
    }
    
    @Test
    public void runBrokenDependency() throws JExampleError {
       Result result = new ExampleGraph().runJExample(A.class); 
       assertEquals(3, result.getRunCount());
       assertEquals(1, result.getFailureCount());
       Failure failure = result.getFailures().iterator().next();
       System.out.println(failure.getTrace());
       // TODO ...
    }
    
    @Test
    public void runBrokenDependencyWithoutConsumer() {
        // TODO write test for broken dependency w/out consumer
    }
    
}
