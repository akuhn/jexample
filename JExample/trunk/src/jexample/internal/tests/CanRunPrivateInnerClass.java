package jexample.internal.tests;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import jexample.JExampleRunner;

public class CanRunPrivateInnerClass {

    @RunWith( JExampleRunner.class )
    private static class PrivateInnerClass {
        @Test public void success() { }
    }
    
    @Test
    public void createJExampleRunner() throws InitializationError {
        new JExampleRunner( PrivateInnerClass.class );
    }
    
    @Test
    public void testRunningPrivateInnerClass()  {
        Result result = JUnitCore.runClasses( PrivateInnerClass.class );
        assertEquals(true, result.wasSuccessful());
        assertEquals(1, result.getRunCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());
    }
    
}
