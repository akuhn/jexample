package ch.unibe.jexample.internal.graph;

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
    
    @Test @Given("path1")
    public N[] path2(N... n) {
        N n1 = n[0];
        N n2 = new N("2nd");
        assertEquals("2nd", n2.value);
        n2.makeEdge(n1);
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
    
    @Test @Given("path2")
    public N[] path3(N... n) {
        N n1 = n[0];
        N n2 = n[1];
        N n3 = new N("3rd");
        assertEquals("3rd", n3.value);
        n3.makeEdge(n2);
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

    @Test @Given("source")
    public N[] fork2(N source) {
        N l = new N("l");
        N r = new N("r");
        assertEquals("l", l.value);
        assertEquals("r", r.value);
        l.makeEdge(source);
        r.makeEdge(source);
        // consumers
        assertEquals(2, source.consumers.size());
        assertEquals(0, l.consumers.size());
        assertEquals(0, r.consumers.size());
        assertEquals("[l, r]", source.consumers().toString());
        // producers
        assertEquals(0, source.producers.size());
        assertEquals(1, l.producers.size());
        assertEquals(1, r.producers.size());
        assertEquals("[source]", l.producers().toString());
        assertEquals("[source]", r.producers().toString());
        return new N[] { source, l, r };
    }

    @Test @Given("sink")
    public N[] join2(N sink) {
        N l = new N("l");
        N r = new N("r");
        assertEquals("l", l.value);
        assertEquals("r", r.value);
        sink.makeEdge(l);
        sink.makeEdge(r);
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
         assertEquals("[l, r]", sink.producers().toString());
        return new N[] { sink, l, r };
    }
    
    @Test @Given("sink,fork2")
    public N[] fork2join(N sink, N... n) {
        N source = n[0];
        N l = n[1];
        N r = n[2];
        sink.makeEdge(l);
        sink.makeEdge(r);
        // consumers
        assertEquals(2, source.consumers.size());
        assertEquals(1, l.consumers.size());
        assertEquals(1, r.consumers.size());
        assertEquals(0, sink.consumers.size());
        assertEquals("[l, r]", source.consumers().toString());
        assertEquals("[sink]", l.consumers().toString());
        assertEquals("[sink]", r.consumers().toString());
        // producers
        assertEquals(0, source.producers.size());
        assertEquals(1, l.producers.size());
        assertEquals(1, r.producers.size());
        assertEquals(2, sink.producers.size());
        assertEquals("[source]", l.producers().toString());
        assertEquals("[source]", r.producers().toString());
        assertEquals("[l, r]", sink.producers().toString());
        return new N[] { source, l, r, sink };
    }
    
}

class N extends Node<String> {
    public N(String example) { super(example); }
}
