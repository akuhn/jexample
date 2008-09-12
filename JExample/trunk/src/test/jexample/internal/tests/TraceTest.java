package jexample.internal.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TraceTest {

    @Test
    public void testRecord() {
        Trace.reset();
        Trace.record(42);
        
        assertEquals(1, Trace.events.size());
        assertEquals("testRecord", Trace.events.get(0).method);
        assertEquals("TraceTest", Trace.events.get(0).impl);
    }
    
}
