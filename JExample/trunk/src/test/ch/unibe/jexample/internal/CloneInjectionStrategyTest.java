package ch.unibe.jexample.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
public class CloneInjectionStrategyTest {

    private InjectionStrategy strategy = new CloneInjectionStrategy();

    @Test
    public void shouldAcceptNullReceiver() {
        InjectionValues values = strategy.makeInjectionValues(null);
        assertEquals(null, values.getReceiver());
        assertEquals(0, values.getArguments().length);
    }
    
    private void shouldNotClone(Object arg) {
        InjectionValues values = strategy.makeInjectionValues(null, arg);
        assertEquals(1, values.getArguments().length);
        assertSame(arg, values.getArguments()[0]);
    }
    
    @Given("shouldAcceptNullReceiver")
    public void shouldNotCloneObject() {
        this.shouldNotClone(new Object());
    }
    
    @Given("shouldAcceptNullReceiver")
    public void shouldNotCloneString() {
        this.shouldNotClone(new String("String"));
    }
    
    @Given("shouldAcceptNullReceiver")
    public void shouldNotCloneInteger() {
        this.shouldNotClone(1291);
    }
    
    @Given("shouldAcceptNullReceiver")
    public void shouldNotCloneDouble() {
        this.shouldNotClone(3.14d);
    }
    
    @Given("shouldAcceptNullReceiver")
    public void shouldNotCloneBoolean() {
        this.shouldNotClone(true);
    }
    
    @Given("shouldAcceptNullReceiver")
    public void shouldNotCloneCharacter() {
        this.shouldNotClone('@');
    }

    @Given("shouldAcceptNullReceiver")
    public void shouldNotCloneNull() {
        this.shouldNotClone(null);
    }
    
}
