package jexample.internal.tests;

import jexample.internal.ExampleGraph;

import org.junit.Test;
import org.junit.internal.runners.InitializationError;

public class TestClassValidation {

    public class A {
        
    }
    
    @Test(expected = InitializationError.class)
    public void textClassValidation() throws InitializationError {
        ExampleGraph g = new ExampleGraph();
        g.add( A.class );
    }
    
}
