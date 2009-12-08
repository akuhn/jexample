package ch.unibe.jexample.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class RerunInjectionStrategyTest {

    @Test
    public void shouldAllRerun() {
        Object receiver = new Object();
        Object[] arguments = { new Object(), new Object(), new Object() };
        InjectionValues values = new RerunInjectionStrategy().makeInjectionValues(receiver, arguments);
        assertNotNull(values);
        assertEquals(ReturnValue.MISSING, values.getReceiver());
        assertEquals(3, values.getArguments().length);
        assertEquals(ReturnValue.MISSING, values.getArguments()[0]);
        assertEquals(ReturnValue.MISSING, values.getArguments()[1]);
        assertEquals(ReturnValue.MISSING, values.getArguments()[2]);
    }
    
    
}
