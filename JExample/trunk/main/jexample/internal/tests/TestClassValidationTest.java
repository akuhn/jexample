package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import jexample.JExample;
import jexample.internal.ExampleGraph;
import jexample.internal.JExampleError;
import jexample.internal.JExampleError.Kind;

import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

public class TestClassValidationTest {

    static class A {
        @Test public void test() { }
    }

    @RunWith( JExample.class )
    static class B {
        public B(Object... args) { }
        @Test public void test() { }
    }

    @RunWith( JExample.class )
    static class C {
        // no example methods
    }

    @RunWith( Runner.class )
    static class D {
        @Test public void test() { }
    }
    
    
    @Test
    public void missingRunWithAnnotation() throws InitializationError {
        try {
            new ExampleGraph().add( A.class );
            fail("InitializationError expected!");
        }
        catch (JExampleError err) {
                assertEquals(1, err.size());
                assertEquals(Kind.MISSING_RUNWITH_ANNOTATION, err.kind());
        }
    }
    

    @Test
    public void testMissingConstructor() throws InitializationError {
        try {
            new ExampleGraph().add( B.class );
            fail("InitializationError expected!");
        }
        catch (JExampleError err) {
            assertEquals(1, err.size());
            assertEquals(Kind.MISSING_CONSTRUCTOR, err.kind());
        }
    }
    

    @Test
    public void missingExampleMetods() throws InitializationError {
        try {
            new ExampleGraph().add( C.class );
            fail("InitializationError expected!");
        }
        catch (JExampleError err) {
            assertEquals(1, err.size());
            assertEquals(Kind.NO_EXAMPLES_FOUND, err.kind());
        }
    }
    

    @Test
    public void wrongRunnerType() throws InitializationError {
        try {
            new ExampleGraph().add( A.class );
            fail("InitializationError expected!");
        }
        catch (JExampleError err) {
            assertEquals(1, err.size());
            assertEquals(Kind.MISSING_RUNWITH_ANNOTATION, err.kind());
        }
    }
    

    
}
