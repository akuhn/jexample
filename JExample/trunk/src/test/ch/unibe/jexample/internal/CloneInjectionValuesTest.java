package ch.unibe.jexample.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.Injection;
import ch.unibe.jexample.InjectionPolicy;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
@Injection(InjectionPolicy.CLONE)
public class CloneInjectionValuesTest {

    private static int FIELD, VALUE;
    
    private D field;

    @Test
    public D producer() {
        assertEquals("should not rerun #producer()", 1, ++VALUE);
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
        assertEquals("should not rerun #field()", 1, ++FIELD);
        field = new D("field");
    }

    @Given("field")
    public void shouldCloneField() {
        assertEquals("clone of field", field.name);
    }
    
    
}
    
class D implements Cloneable {
    
    public String name;
    
    public D(String name) {
        this.name = name;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new D("clone of " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
