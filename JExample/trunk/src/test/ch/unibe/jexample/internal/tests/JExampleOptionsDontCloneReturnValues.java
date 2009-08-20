package ch.unibe.jexample.internal.tests;

import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.Injection;
import ch.unibe.jexample.InjectionPolicy;
import ch.unibe.jexample.JExample;
import ch.unibe.jexample.internal.tests.JExampleOptionsDefault.Mutable;

@RunWith(JExample.class)
@Injection(InjectionPolicy.NONE)
public class JExampleOptionsDontCloneReturnValues {

    @Test
    public Mutable create() {
        return new Mutable("root");
    }

    @Given("create")
    public Mutable left(Mutable a) {
        return a;
    }

    @Given("create")
    public Mutable right(Mutable a) {
        return a;
    }

    @Given("left;right")
    public void test(Mutable left, Mutable right) {
        assertSame(left, right);
    }

}
