package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import jexample.JExampleRunner;
import jexample.internal.ExampleGraph;
import jexample.internal.InvalidExampleError;
import jexample.internal.InvalidExampleError.Kind;

import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

public class TestClassValidationTest {

    private static class A {
        @Test public void test() { }
    }

    @RunWith( JExampleRunner.class )
    private static class B {
        public B(Object... args) { }
        @Test public void test() { }
    }

    @RunWith( JExampleRunner.class )
    private static class C {
        // no example methods
    }

    @RunWith( Runner.class )
    private static class D {
        @Test public void test() { }
    }
    
    
    @Test
    public void missingRunWithAnnotation() throws InitializationError {
        try {
            new ExampleGraph().add( A.class );
            fail("InitializationError expected!");
        }
        catch (InitializationError ex) {
            assertEquals(1, ex.getCauses().size());
            InvalidExampleError $ = (InvalidExampleError) ex.getCauses().get(0);
            assertEquals(Kind.MISSING_RUNWITH_ANNOTATION, $.kind);
        }
    }
    

    @Test
    public void testMissingConstructor() throws InitializationError {
        try {
            new ExampleGraph().add( B.class );
            fail("InitializationError expected!");
        }
        catch (InitializationError ex) {
            assertEquals(1, ex.getCauses().size());
            InvalidExampleError $ = (InvalidExampleError) ex.getCauses().get(0);
            assertEquals(Kind.MISSING_CONSTRUCTOR, $.kind);
        }
    }
    

    @Test
    public void missingExampleMetods() throws InitializationError {
        try {
            new ExampleGraph().add( C.class );
            fail("InitializationError expected!");
        }
        catch (InitializationError ex) {
            assertEquals(1, ex.getCauses().size());
            InvalidExampleError $ = (InvalidExampleError) ex.getCauses().get(0);
            assertEquals(Kind.NO_EXAMPLES_FOUND, $.kind);
        }
    }
    

    @Test
    public void wrongRunnerType() throws InitializationError {
        try {
            new ExampleGraph().add( A.class );
            fail("InitializationError expected!");
        }
        catch (InitializationError ex) {
            assertEquals(1, ex.getCauses().size());
            InvalidExampleError $ = (InvalidExampleError) ex.getCauses().get(0);
            assertEquals(Kind.MISSING_RUNWITH_ANNOTATION, $.kind);
        }
    }
    

    
}
