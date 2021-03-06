package ch.unibe.jexample.internal;

import static org.junit.Assert.*;

import org.junit.Test;

public class RerunInjectionStrategyTest {

    @Test
    public void shouldAllRerun() {
        Object receiver = new Object();
        Object[] arguments = { new Object(), new Object(), new Object() };
        InjectionValues values = new RerunInjectionStrategy().makeInjectionValues(receiver, arguments);
        assertNotNull(values);
        assertEquals(InjectionStrategy.MISSING, values.getReceiver());
        assertEquals(3, values.getArguments().length);
        assertEquals(InjectionStrategy.MISSING, values.getArguments()[0]);
        assertEquals(InjectionStrategy.MISSING, values.getArguments()[1]);
        assertEquals(InjectionStrategy.MISSING, values.getArguments()[2]);
    }
    
    
}
