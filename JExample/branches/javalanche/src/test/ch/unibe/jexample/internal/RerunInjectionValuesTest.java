package ch.unibe.jexample.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.Injection;
import ch.unibe.jexample.InjectionPolicy;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
@Injection(InjectionPolicy.RERUN)
public class RerunInjectionValuesTest {

    static int A, B, SINK, C, D, E;

    String field = "<init>";
    
    /** Runs three times: once for itself,
     * twice to provide the receiver of #b.
     */
    @Test
    public void a() {
        ++A; 
    }

    /** Runs two times: once for itself,
     * once to provide receiver and first argument of #sink.
     */
    @Given("a")
    public int b() {
        return ++B; 
    }

    /** The sink, runs once and checks static counters.
     */
    @Given("b,c,d,e")
    public void sink(int b, int c, int d) {
        SINK++; 
        assertEquals(1, SINK);
        assertEquals(3, A);
        assertEquals(2, B);
        assertEquals(b, B);
        assertEquals(2, C);
        assertEquals(c, C);
        assertEquals(2, D);
        assertEquals(d, D);
        assertEquals(1, E);
    }

    /** Runs two times: once for itself, 
     * once to provide the second argument of #sink.
     */
    @Test
    public int c() {
        return ++C;
    }
    
    /** Runs two times: once for itself, 
     * once to provide the third argument of #sink.
     */
    @Test
    public int d() {
        return ++D;
    }
    
    /** Runs once for itself &mdash; is not rerun, because #sink only depends
     * on this method but does not consume the result of this method.
     */
    @Test
    public int e() {
        return ++E;
    }

}
