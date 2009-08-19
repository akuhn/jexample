package ch.unibe.jexample.internal.graph;

import static ch.unibe.jexample.util.AssertUtil.assertToString;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
public class SmallGraph {

    @Test 
    public N sink() {
        return new N("sink");
    }

    @Test 
    public N source() {
        return new N("source");
    }
    
    @Test
    public N[] path1() {
        N n1 = new N("1st");
        assertEquals("1st", n1.value);
        assertEquals(0, n1.consumers.size());
        assertEquals(0, n1.producers.size());
        return new N[] { n1 };
    }
    
    @Given("path1")
    public N[] path2(N... n) {
        N n1 = n[0];
        N n2 = new N("2nd");
        assertEquals("2nd", n2.value);
        n2.addProvider(n1);
        // consumers
        assertEquals(1, n1.consumers.size());
        assertEquals(0, n2.consumers.size());
        assertEquals("[2nd]", n1.consumers().toString());
        // producers
        assertEquals(0, n1.producers.size());
        assertEquals(1, n2.producers.size());
        assertEquals("[1st]", n2.producers().toString());
        return new N[] { n1, n2 };
    }
    
    @Given("path2")
    public N[] path3(N... n) {
        N n1 = n[0];
        N n2 = n[1];
        N n3 = new N("3rd");
        assertEquals("3rd", n3.value);
        n3.addProvider(n2);
        // consumers
        assertEquals(1, n1.consumers.size());
        assertEquals(1, n2.consumers.size());
        assertEquals(0, n3.consumers.size());
        assertEquals("[2nd]", n1.consumers().toString());
        assertEquals("[3rd]", n2.consumers().toString());
        // producers
        assertEquals(0, n1.producers.size());
        assertEquals(1, n2.producers.size());
        assertEquals(1, n3.producers.size());
        assertEquals("[1st]", n2.producers().toString());
        assertEquals("[2nd]", n3.producers().toString());
        return new N[] { n1, n2, n3 };
    }

    @Given("source")
    public N[] fork2(N source) {
        N l = new N("L");
        N r = new N("R");
        assertEquals("L", l.value);
        assertEquals("R", r.value);
        l.addProvider(source);
        r.addProvider(source);
        // consumers
        assertEquals(2, source.consumers.size());
        assertEquals(0, l.consumers.size());
        assertEquals(0, r.consumers.size());
        assertEquals("[L, R]", source.consumers().toString());
        // producers
        assertEquals(0, source.producers.size());
        assertEquals(1, l.producers.size());
        assertEquals(1, r.producers.size());
        assertEquals("[source]", l.producers().toString());
        assertEquals("[source]", r.producers().toString());
        return new N[] { source, l, r };
    }

    @Given("sink")
    public N[] join2(N sink) {
        N l = new N("L");
        N r = new N("R");
        assertEquals("L", l.value);
        assertEquals("R", r.value);
        sink.addProvider(l);
        sink.addProvider(r);
        // consumers
        assertEquals(1, l.consumers.size());
        assertEquals(1, r.consumers.size());
        assertEquals(0, sink.consumers.size());
        assertEquals("[sink]", l.consumers().toString());
        assertEquals("[sink]", r.consumers().toString());
        // producers
        assertEquals(2, sink.producers.size());
        assertEquals(0, l.producers.size());
        assertEquals(0, r.producers.size());
         assertEquals("[L, R]", sink.producers().toString());
        return new N[] { sink, l, r };
    }
    
    @Given("sink,fork2")
    public N[] fork2join(N sink, N... n) {
        N source = n[0];
        N l = n[1];
        N r = n[2];
        sink.addProvider(l);
        sink.addProvider(r);
        // consumers
        assertEquals(2, source.consumers.size());
        assertEquals(1, l.consumers.size());
        assertEquals(1, r.consumers.size());
        assertEquals(0, sink.consumers.size());
        assertEquals("[L, R]", source.consumers().toString());
        assertEquals("[sink]", l.consumers().toString());
        assertEquals("[sink]", r.consumers().toString());
        // producers
        assertEquals(0, source.producers.size());
        assertEquals(1, l.producers.size());
        assertEquals(1, r.producers.size());
        assertEquals(2, sink.producers.size());
        assertEquals("[source]", l.producers().toString());
        assertEquals("[source]", r.producers().toString());
        assertEquals("[L, R]", sink.producers().toString());
        return new N[] { source, l, r, sink };
    }
 
    @Given("fork2join")
    public N[] cross84(N... n) {
        N top = new N("top");
        N source = n[0];
        N sink = n[3];
        N bottom = new N("bottom");
        source.addProvider(top);
        bottom.addProvider(sink);
        // consumers
        assertToString("[source]", top.consumers());
        assertToString("[L, R]", source.consumers());
        assertToString("[bottom]", sink.consumers());
        assertToString("[]", bottom.consumers());
        // producers
        assertToString("[]", top.producers());
        assertToString("[top]", source.producers());
        assertToString("[L, R]", sink.producers());
        assertToString("[sink]", bottom.producers());
        return new N[] { top, source, n[1], n[2], sink, bottom };
    }
    
}

class N extends Node<String> {
    public N(String example) { super(example); }
}
