package jexample.internal.tests;

import org.junit.Test;
import org.junit.internal.runners.InitializationError;

import jexample.internal.TestGraph;

public class TestClassValidation {

    public class A {
        
    }
    
    @Test(expected = InitializationError.class)
    public void textClassValidation() throws InitializationError {
        TestGraph g = new TestGraph();
        g.add( A.class );
    }
    
}
