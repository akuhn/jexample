package ch.unibe.jexample.internal.graph;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
public class CycleDetectionTest {

    @Test
    @Given("SmallGraph#path2")
    public void cycle2(N[] n) {
        N aa = n[0];
        N bb = n[1];
        assertEquals(false, aa.isPartOfCycle());
        assertEquals(false, bb.isPartOfCycle());
        aa.makeEdge(bb);
        assertEquals(true, aa.isPartOfCycle());
        assertEquals(true, bb.isPartOfCycle());
    }
    
    @Test
    @Given("SmallGraph#path3")
    public void cycle3(N[] n) {
        N aa = n[0];
        N bb = n[1];
        N cc = n[2];
        assertEquals(false, aa.isPartOfCycle());
        assertEquals(false, bb.isPartOfCycle());
        assertEquals(false, cc.isPartOfCycle());
        aa.makeEdge(cc);
        assertEquals(true, aa.isPartOfCycle());
        assertEquals(true, bb.isPartOfCycle());
        assertEquals(true, cc.isPartOfCycle());
    }
    
    @Test
    @Given("SmallGraph#path3")
    public void cactus2(N[] n) {
        N aa = n[0];
        N bb = n[1];
        N cc = n[2];
        assertEquals(false, aa.isPartOfCycle());
        assertEquals(false, bb.isPartOfCycle());
        assertEquals(false, cc.isPartOfCycle());
        aa.makeEdge(bb);
        assertEquals(true, aa.isPartOfCycle());
        assertEquals(true, bb.isPartOfCycle());
        assertEquals(false, cc.isPartOfCycle());
    }

    @Test
    @Given("SmallGraph#fork2join")
    public void diamond(N[] n) {
        N aa = n[0];
        N bb = n[1];
        N cc = n[2];
        N dd = n[3];
        assertEquals(false, aa.isPartOfCycle());
        assertEquals(false, bb.isPartOfCycle());
        assertEquals(false, cc.isPartOfCycle());
        assertEquals(false, dd.isPartOfCycle());
        aa.makeEdge(dd);
        assertEquals(true, aa.isPartOfCycle());
        assertEquals(true, bb.isPartOfCycle());
        assertEquals(true, cc.isPartOfCycle());
        assertEquals(true, dd.isPartOfCycle());
    }
    
    
}
