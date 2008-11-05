package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import jexample.Depends;
import jexample.JExample;
import jexample.internal.tests.Util.*;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JExample.class)
public class JExampleOptionsDefault {

    @Test
    public NotCloneable notCloneable() {
        return new NotCloneable("root");
    }

    @Test
    @Depends("#notCloneable,#notCloneable")
    public void testRerunning(NotCloneable a, NotCloneable b) {
        assertNotSame(a, b);
        assertEquals(a.name, b.name);
    }
    
    @Test
    @Depends("#notCloneable")
    public NotCloneable left(NotCloneable a) {
        assertEquals("root", a.name);
        return a;
    }

    @Test
    @Depends("#notCloneable")
    public NotCloneable right(NotCloneable a) {
        assertEquals("root", a.name);
        return a;
    }
    
    @Test
    @Depends("#left,#right")
    public void testRerunning2(NotCloneable a, NotCloneable b){
        assertNotSame(a, b);
        assertEquals(a.name, b.name);
    }

    @Test
    public IsCloneable isCloneable() {
        return new IsCloneable("root");
    }

    @Test
    @Depends("#isCloneable,#isCloneable")
    public void testCloning(IsCloneable a, IsCloneable b) {
        assertNotSame(a, b);
        assertEquals("clone of root", a.name);
        assertEquals("clone of root", b.name);
    }
    
    @Test
    @Depends("#isCloneable")
    public IsCloneable leftClone(IsCloneable a) {
        assertEquals("clone of root", a.name);
        return a;
    }

    @Test
    @Depends("#isCloneable")
    public IsCloneable rightClone(IsCloneable a) {
        assertEquals("clone of root", a.name);
        return a;
    }
    
    @Test
    @Depends("#leftClone,#rightClone")
    public void testCloning2(IsCloneable a, IsCloneable b){
        assertNotSame(a, b);
        assertEquals("clone of clone of root", a.name);
        assertEquals("clone of clone of root", b.name);
    }
    
}
