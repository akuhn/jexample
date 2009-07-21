package ch.unibe.jexample.internal;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import ch.unibe.jexample.For;
import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;
import ch.unibe.jexample.util.JExampleError;
import ch.unibe.jexample.util.JExampleError.Kind;

public class BrokenDependencyTest {

    @RunWith(JExample.class)
    public static class A {
        @Test public int m() { return 1; }
        @Test public int n() { return 2;}
        @Test 
        @Given("#m,#a,#n")
        public Object t(int m, int a, int n) { 
            throw new RuntimeException("Not reachable!");
        }
    }
    
    @Test
    public void createExampleGraphA() throws JExampleError {
        ExampleGraph egg = new ExampleGraph();
        egg.add(A.class);
        assertEquals(3, egg.getExamples().size());
    }
    
    @Test
    public void runBrokenDependency() throws JExampleError {
       Result result = new ExampleGraph().runJExample(A.class); 
       assertEquals(3, result.getRunCount());
       assertEquals(1, result.getFailureCount());
       Failure failure = result.getFailures().iterator().next();
       assertEquals("t(" + A.class.getName() + ")", failure.getTestHeader());
       assertJExampleErrorProvicerNotFound(failure.getException());
    }
    
    @RunWith(JExample.class)
    public static class B {
        @Test public int m() { return 1; }
        @Test public int n() { return 2;}
        @Test 
        @Given("#m,#a,#n")
        public Object t() { 
            throw new RuntimeException("Not reachable!");
        }
    }

    @Test
    public void createExampleGraphB() throws JExampleError {
        ExampleGraph egg = new ExampleGraph();
        egg.add(B.class);
        assertEquals(3, egg.getExamples().size());
    }

    @Test
    public void runBrokenDependencyWithoutConsumer() throws JExampleError {
        Result result = new ExampleGraph().runJExample(B.class); 
        assertEquals(3, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        Failure failure = result.getFailures().iterator().next();
        assertEquals("t(" + B.class.getName() + ")", failure.getTestHeader());
        assertJExampleErrorProvicerNotFound(failure.getException());
    }

    @Test
    public void runBrokenDependencyWithFilter() throws JExampleError {
    	try {
    		For.example(A.class, "t");
    		fail();
    	}
    	catch (RuntimeException ex) {
    		assertJExampleErrorProvicerNotFound(ex.getCause());
    	}
    }

	private void assertJExampleErrorProvicerNotFound(Throwable ex) {
		assertEquals(JExampleError.class, ex.getClass());
		JExampleError error = (JExampleError) ex;
		assertEquals(1, error.size());
		assertEquals(NoSuchMethodException.class, error.getCause().getClass());
		assertEquals(Kind.NO_SUCH_PROVIDER, error.getKind());
		NoSuchMethodException exception = (NoSuchMethodException) error.getCause();
		assertEquals("#a", exception.getMessage());
	}
    
    
}
