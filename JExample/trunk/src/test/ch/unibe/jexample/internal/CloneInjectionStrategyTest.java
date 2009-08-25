package ch.unibe.jexample.internal;

import static ch.unibe.jexample.internal.util.AssertUtil.assertToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
public class CloneInjectionStrategyTest {

    private InjectionStrategy strategy = new CloneInjectionStrategy();

    @Test
    public void shouldAcceptNullReceiver() {
        InjectionValues values = strategy.makeInjectionValues(null);
        assertEquals(null, values.getReceiver());
        assertEquals(0, values.getArguments().length);
    }
    
    private void shouldNotClone(Object arg) {
        InjectionValues values = strategy.makeInjectionValues(new Object(), arg);
        assertEquals(1, values.getArguments().length);
        assertSame(arg, values.getArguments()[0]);
    }
    
    @Test
    public void shouldNotCloneObject() {
        this.shouldNotClone(new Object());
    }
    
    @Test
    public void shouldNotCloneString() {
        this.shouldNotClone(new String("String"));
    }
    
    @Test
    public void shouldNotCloneInteger() {
        this.shouldNotClone(1291);
    }
    
    @Test
    public void shouldNotCloneDouble() {
        this.shouldNotClone(3.14d);
    }
    
    @Test
    public void shouldNotCloneBoolean() {
        this.shouldNotClone(true);
    }
    
    @Test
    public void shouldNotCloneCharacter() {
        this.shouldNotClone('@');
    }

    @Test
    public void shouldNotCloneNull() {
        this.shouldNotClone(null);
    }
    
    @Test
    public void shouldCloneArgument() {
        D d = new D("dummy");
        InjectionValues values = strategy.makeInjectionValues(new Object(), d);
        assertEquals(1, values.getArguments().length);
        assertToString("clone of dummy", values.getArguments()[0]);
    }
    
    @Given("shouldCloneArgument")
    public void shouldCloneAllArguments() {
        InjectionValues values = strategy.makeInjectionValues(new Object(),
                new D("1st"),
                new D("2nd"),
                new D("3rd"));
        assertEquals(3, values.getArguments().length);
        assertToString("clone of 1st", values.getArguments()[0]);
        assertToString("clone of 2nd", values.getArguments()[1]);
        assertToString("clone of 3rd", values.getArguments()[2]);
    }
    
    @Test
    public void shouldRerunNonClonable() {
        Object cannotClone = new Object() { };
        InjectionValues values = strategy.makeInjectionValues(new Object(), cannotClone);
        assertEquals(1, values.getArguments().length);
        assertEquals(InjectionStrategy.MISSING, values.getArguments()[0]);
    }
    
    public void shouldRerunIfCloneFails() {
        Object cannotClone = new BrokenCloneable();
        InjectionValues values = strategy.makeInjectionValues(new Object(), cannotClone);
        assertEquals(1, values.getArguments().length);
        assertEquals(InjectionStrategy.MISSING, values.getArguments()[0]);
    }
    
    static class BrokenCloneable implements Cloneable { 
        @Override
        protected Object clone() throws CloneNotSupportedException {
            throw new Error();
        }
    };
    
    static class R {
        public Object field;
    }

    @Test
    public void shouldCloneFieldOfReceiver() {
        R r = new R();
        r.field = new D("field");
        InjectionValues values = strategy.makeInjectionValues(r);
        assertNotSame(r, values.getReceiver());
        assertTrue(values.getReceiver() instanceof R);
        assertToString("clone of field", ((R) values.getReceiver()).field);
    }
 
    @Test
    public void shouldAcceptNullField() {
        R r = new R();
        r.field = null;
        InjectionValues values = strategy.makeInjectionValues(r);
        assertNotSame(r, values.getReceiver());
        assertTrue(values.getReceiver() instanceof R);
        assertEquals(null, ((R) values.getReceiver()).field);
    }
    
    @Test
    public void shouldNotCloneImmutableField() {
        R r = new R();
        r.field = new String("field");
        InjectionValues values = strategy.makeInjectionValues(r);
        assertNotSame(r, values.getReceiver());
        assertTrue(values.getReceiver() instanceof R);
        assertEquals(r.field, ((R) values.getReceiver()).field);
    }
    
}


