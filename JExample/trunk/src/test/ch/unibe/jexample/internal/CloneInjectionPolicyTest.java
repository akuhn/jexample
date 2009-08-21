package ch.unibe.jexample.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.Injection;
import ch.unibe.jexample.InjectionPolicy;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
@Injection(InjectionPolicy.CLONE)
public class CloneInjectionPolicyTest {

    private D field;

    @Test
    public D producer() {
        return new D("value");
    }
    
    @Given("producer")
    public D shouldCloneArgument(D d) {
        assertEquals("clone of value", d.name);
        return d;
    }
    
    @Given("shouldCloneArgument")
    public void shouldCloneArgumentAgain(D d) {
        assertEquals("clone of clone of value", d.name);
    }
    
    @Test
    public void field() {
        field = new D("field");
    }

    @Given("field")
    @Test(expected=AssertionError.class) // FIXME broken
    public void shouldCloneField() {
        assertEquals("clone of field", field.name);
    }
    
    
}
    
class D implements Cloneable {
    
    public final String name;
    
    public D(String name) {
        this.name = name;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new D("clone of " + name);
    }
    
}
