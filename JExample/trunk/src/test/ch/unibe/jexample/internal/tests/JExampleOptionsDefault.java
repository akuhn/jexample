package ch.unibe.jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
public class JExampleOptionsDefault {

    @Test
    public Immutable notCloneable() {
        return new Immutable("root");
    }

    @Given("#notCloneable,#notCloneable")
    public void testRerunning(Immutable a, Immutable b) {
        assertSame(a, b);
        assertEquals(a.name, b.name);
    }

    @Given("#notCloneable")
    public Immutable left(Immutable a) {
        assertEquals("root", a.name);
        return a;
    }

    @Given("#notCloneable")
    public Immutable right(Immutable a) {
        assertEquals("root", a.name);
        return a;
    }

    @Given("#left,#right")
    public void testRerunning2(Immutable a, Immutable b) {
        assertSame(a, b);
        assertEquals(a.name, b.name);
    }

    @Test
    public Mutable isCloneable() {
        return new Mutable("root");
    }

    @Given("#isCloneable,#isCloneable")
    public void shouldUseSameCloneFactoryFroAllArguments(Mutable a, Mutable b) {
        assertSame(a, b); 
        assertEquals("root", a.name);
        assertEquals("root", b.name);
    }

    @Given("#isCloneable")
    public Mutable leftClone(Mutable a) {
        assertEquals("root", a.name);
        return a;
    }

    @Given("#isCloneable")
    public Mutable rightClone(Mutable a) {
        assertEquals("root", a.name);
        return a;
    }

    @Given("#leftClone,#rightClone")
    public void testCloning2(Mutable a, Mutable b) {
        assertNotSame(a, b);
        assertEquals("root", a.name);
        assertEquals("root", b.name);
    }
    
    static class Mutable {

        public String name;
        
        public Mutable(String value) {
            this.name = value;
        }
        
    }
    
    static class Immutable {
        
        public final String name;
        
        public Immutable(String value) {
            this.name = value;
        }
    }

}
