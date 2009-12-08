package ch.unibe.jexample.internal;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;

/** Given a series of linear dependencies 
 * (and a non-cloneable instance variable),
 * all examples should run once and only once.
 *<P>
 * On Dec 08, 2009, Oscar reported quadratic running of linear dependencies
 * when test case has non-cloneable instance variable. This is fixed now,
 * the fix works as follows: when the clone strategy (which is default)
 * fails, the forced rerun "consumes" the return value of is provider. That
 * is, if there is already one from a previous run, it is returned and flushed.
 * If there is none, the provider is rerun and no return value caches. 
 *<P> 
 * @author Adrian Kuhn
 *
 */
@RunWith(JExample.class)
public class GivenLinearDependenciesTest {

    private static List<String> list = new ArrayList<String>();

    public Object variable = new Object() { }; // factor out to util method
    
    @Test
    public void shouldRunOnceA() {
        list.add("A");
    }
    
    @Given("shouldRunOnceA")
    public void shouldRunOnceB() {
        list.add("B");
    }

    @Given("shouldRunOnceB")
    public void shouldRunOnceC() {
        list.add("C");
    }

    @Given("shouldRunOnceC")
    public void shouldRunOnceD() {
        list.add("D");
    }

    // @Ignore // FIXME kwnon to be broken
    @Given("shouldRunOnceD")
    public void shouldRunAllExamplesOnceAndOnlyOnce() {
        assertEquals("[A, B, C, D]", list.toString());
    }
    
}
