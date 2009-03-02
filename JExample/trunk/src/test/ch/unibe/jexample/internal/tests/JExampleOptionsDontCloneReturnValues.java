package ch.unibe.jexample.internal.tests;

import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;
import ch.unibe.jexample.JExampleOptions;
import ch.unibe.jexample.internal.tests.Util.IsCloneable;

@RunWith(JExample.class)
@JExampleOptions(cloneReturnValues = false)
public class JExampleOptionsDontCloneReturnValues {

    @Test
    public IsCloneable create() {
        return new IsCloneable("root");
    }

    @Test
    @Given("create")
    public IsCloneable left(IsCloneable a) {
        return a;
    }

    @Test
    @Given("create")
    public IsCloneable right(IsCloneable a) {
        return a;
    }

    @Test
    @Given("left;right")
    public void test(IsCloneable left, IsCloneable right) {
        assertSame(left, right);
    }

}
