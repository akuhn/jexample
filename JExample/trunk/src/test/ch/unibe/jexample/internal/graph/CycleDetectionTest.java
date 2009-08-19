package ch.unibe.jexample.internal.graph;

import static ch.unibe.jexample.util.AssertUtil.assertToString;
import static ch.unibe.jexample.util.AssertUtil.assertToStringFormat;
import static org.junit.Assert.assertEquals;

import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
public class CycleDetectionTest {

    private static final boolean F = false;

    @Given("SmallGraph#path2")
    public N[] cycle2(N... n) {
        N aa = n[0];
        N bb = n[1];
        assertEquals(F, aa.isPartOfCycle());
        assertEquals(F, bb.isPartOfCycle());
        aa.addProvider(bb);
        assertEquals(true, aa.isPartOfCycle());
        assertEquals(true, bb.isPartOfCycle());
        return n;
    }

    @Given("cycle2")
    public void cycle2cycles(N... n) {
        String cycles = "[(1st < 2nd < 1st)]";
        assertToString(cycles, n[0].dependencies().get(0).cycles());
        assertToString(cycles, n[1].dependencies().get(0).cycles());
    }
    
    @Given("SmallGraph#path3")
    public N[] cycle3(N... n) {
        assertEquals(F, n[0].isPartOfCycle());
        assertEquals(F, n[1].isPartOfCycle());
        assertEquals(F, n[2].isPartOfCycle());
        n[0].addProvider(n[2]);
        assertEquals(true, n[0].isPartOfCycle());
        assertEquals(true, n[1].isPartOfCycle());
        assertEquals(true, n[2].isPartOfCycle());
        return n;
    }
    
    @Given("cycle3")
    public void shouldHave1321Cycle(N... n) {
        Edge<String> edge = n[0].dependencies().get(0);
        assertEquals(1, edge.cycles().size());
        assertToString("[(1st < 3rd < 2nd < 1st)]", edge.cycles());
    }
    
    @Given("cycle3,shouldHave1321Cycle")
    public void shouldAllHaveSameCycle(N... n) {
        assertToString("[(1st < 3rd < 2nd < 1st)]", n[0].dependencies().get(0).cycles());
        assertToString("[(1st < 3rd < 2nd < 1st)]", n[1].dependencies().get(0).cycles());
        assertToString("[(1st < 3rd < 2nd < 1st)]", n[2].dependencies().get(0).cycles());
    }
    
    @Given("SmallGraph#path3")
    public void cactus2(N... n) {
        assertEquals(F, n[0].isPartOfCycle());
        assertEquals(F, n[1].isPartOfCycle());
        assertEquals(F, n[2].isPartOfCycle());
        n[0].addProvider(n[1]);
        assertEquals(true, n[0].isPartOfCycle());
        assertEquals(true, n[1].isPartOfCycle());
        assertEquals(F, n[2].isPartOfCycle());
    }

    @Given("SmallGraph#fork2join")
    public void diamond(N... n) {
        assertEquals(F, n[0].isPartOfCycle());
        assertEquals(F, n[1].isPartOfCycle());
        assertEquals(F, n[2].isPartOfCycle());
        assertEquals(F, n[3].isPartOfCycle());
        n[0].addProvider(n[3]);
        assertEquals(true, n[0].isPartOfCycle());
        assertEquals(true, n[1].isPartOfCycle());
        assertEquals(true, n[2].isPartOfCycle());
        assertEquals(true, n[3].isPartOfCycle());
        //
        Edge<String> edge = n[0].dependencies().get(0);
        assertEquals("source", edge.consumer.value);
        assertEquals("sink", edge.producer.value);
        assertEquals(2, edge.cycles().size());
        assertEquals("[(source < sink < L < source), (source < sink < R < source)]",
                edge.cycles().toString());
    }

    @Given("SmallGraph#cross84")
    public N[] diamond84(N... n) {
        assertEquals(F, n[0].isPartOfCycle());
        assertEquals(F, n[1].isPartOfCycle());
        assertEquals(F, n[2].isPartOfCycle());
        assertEquals(F, n[3].isPartOfCycle());
        assertEquals(F, n[4].isPartOfCycle());
        assertEquals(F, n[5].isPartOfCycle());
        n[1].addProvider(n[4]);
        assertEquals(F, n[0].isPartOfCycle());
        assertEquals(true, n[1].isPartOfCycle());
        assertEquals(true, n[2].isPartOfCycle());
        assertEquals(true, n[3].isPartOfCycle());
        assertEquals(true, n[4].isPartOfCycle());
        assertEquals(F, n[5].isPartOfCycle());
        return n;
    }
    
    @Given("diamond84")
    public void diamond84cycles(N... n) {
        String l = "(source < sink < L < source)";
        String r = "(source < sink < R < source)";
        assertToString("[]", n[1].dependencies().get(0).cycles());
        assertToStringFormat("[%s, %s]", n[1].dependencies().get(1).cycles(), l, r);
        assertToStringFormat("[%s]", n[2].dependencies().get(0).cycles(), l);
        assertToStringFormat("[%s]", n[3].dependencies().get(0).cycles(), r);
        assertToStringFormat("[%s]", n[4].dependencies().get(0).cycles(), l);
        assertToStringFormat("[%s]", n[4].dependencies().get(1).cycles(), r);
        assertToString("[]", n[5].dependencies().get(0).cycles());
    }
    
}
