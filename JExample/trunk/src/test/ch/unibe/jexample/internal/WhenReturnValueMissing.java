package ch.unibe.jexample.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
public class WhenReturnValueMissing {

    Object o = new Object() {};

    @Test
    public String shouldRunTwice() {
        return "foo";
    }

    @Given("shouldRunTwice")
    public void shouldConsumeFoo(String foo) {
        assertEquals("foo", foo);
    }

    @Given("shouldRunTwice")
    public void shouldConsumeFooToo(String foo) {
        assertEquals("foo", foo);
    }

}
