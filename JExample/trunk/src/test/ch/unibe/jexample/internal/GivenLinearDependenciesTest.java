package ch.unibe.jexample.internal;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;
import ch.unibe.jexample.internal.util.Util;

/** Given a series of linear dependencies 
 * (and a non-cloneable instance variable),
 * all examples should run once and only once.
 * 
 * @author Adrian Kuhn
 *
 */
@RunWith(JExample.class)
public class GivenLinearDependenciesTest {

    private static List<String> list = new ArrayList<String>();

    /** Oscar reported quadratic running of linear dependencies
     * when test case has non-cloneable instance variable. */ 
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

    @Ignore // FIXME kwnon to be broken
    @Given("shouldRunOnceD")
    public void shouldRunAllExamplesOnceAndOnlyOnce() {
        assertEquals("[A, B, C, D]", list.toString());
    }
    
}
