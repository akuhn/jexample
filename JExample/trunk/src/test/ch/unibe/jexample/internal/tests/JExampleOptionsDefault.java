package ch.unibe.jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;
import ch.unibe.jexample.internal.tests.Util.*;

@RunWith(JExample.class)
public class JExampleOptionsDefault {

    @Test
    public NotCloneable notCloneable() {
        return new NotCloneable("root");
    }

    @Test
    @Given("#notCloneable,#notCloneable")
    public void testRerunning(NotCloneable a, NotCloneable b) {
        assertNotSame(a, b);
        assertEquals(a.name, b.name);
    }
    
    @Test
    @Given("#notCloneable")
    public NotCloneable left(NotCloneable a) {
        assertEquals("root", a.name);
        return a;
    }

    @Test
    @Given("#notCloneable")
    public NotCloneable right(NotCloneable a) {
        assertEquals("root", a.name);
        return a;
    }
    
    @Test
    @Given("#left,#right")
    public void testRerunning2(NotCloneable a, NotCloneable b){
        assertNotSame(a, b);
        assertEquals(a.name, b.name);
    }

    @Test
    public IsCloneable isCloneable() {
        return new IsCloneable("root");
    }

    @Test
    @Given("#isCloneable,#isCloneable")
    public void testCloning(IsCloneable a, IsCloneable b) {
        assertNotSame(a, b);
        assertEquals("clone of root", a.name);
        assertEquals("clone of root", b.name);
    }
    
    @Test
    @Given("#isCloneable")
    public IsCloneable leftClone(IsCloneable a) {
        assertEquals("clone of root", a.name);
        return a;
    }

    @Test
    @Given("#isCloneable")
    public IsCloneable rightClone(IsCloneable a) {
        assertEquals("clone of root", a.name);
        return a;
    }
    
    @Test
    @Given("#leftClone,#rightClone")
    public void testCloning2(IsCloneable a, IsCloneable b){
        assertNotSame(a, b);
        assertEquals("clone of clone of root", a.name);
        assertEquals("clone of clone of root", b.name);
    }
    
}
