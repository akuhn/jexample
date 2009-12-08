package ch.unibe.jexample.internal;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.Injection;
import ch.unibe.jexample.InjectionPolicy;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
@Injection(InjectionPolicy.RERUN)
public class RerunInjectionValuesTest {

    private static List<String> list = new ArrayList<String>();

    @Test
    public int a() {
        list.add("A");
        return 0;
    }
    
    @Given("a")
    public int b(int a) {
        list.add("B");
        return 0;
    }
    
    @Given("a,b")
    public int c(int a) {
        list.add("C");
        return 0;
    }
    
    /** Chains of dependencies. 
     * Parentheses mark skipped runs, since value was still cached;
     * apostrophe marks cached return value.
     *<PRE>  A'
     * (A)  B'
     *  A   -   C'
     *  A' (B)  -   D'
     *</PRE>  
     */
    @Given("a,b,c")
    public void d(int a, int b) {
        list.add("D");
        assertEquals("[A, B, A, C, A, D]", list.toString());
    }
    
    
    // TODO test for rerun policy
    
}
