package ch.unibe.jexample.internal.deepclone;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import ch.unibe.jexample.internal.deepcopy.ImmutableClasses;

public class ImmutableClassesTest {

    private static final boolean F = false;

    @Test
    public void shouldContainString() {
        assertEquals(true, ImmutableClasses.contains(String.class));
    }

    @Test
    public void shouldContainInteger() {
        assertEquals(true, ImmutableClasses.contains(Integer.class));
    }
    
    @Test
    public void shouldNotContainAtomicInteger() {
        assertEquals(true, ImmutableClasses.contains(AtomicInteger.class));
    }
    
    @Test
    public void shouldNotContainStringBuffer() {
        assertEquals(F, ImmutableClasses.contains(StringBuffer.class));
    }
    
    @Test
    public void shouldMatchPattern2() {
        assertEquals(true, ImmutableClasses.match("abc*", "abcde"));
    }

    @Test
    public void shouldMatchIfEquals() {
        assertEquals(true, ImmutableClasses.match("abc", "abc"));
    }

    @Test
    public void shouldNotMatchIfNotEquals() {
        assertEquals(F, ImmutableClasses.match("abc", "abcde"));
    }

    @Test
    public void shouldNotMatchOffByOne() {
        assertEquals(F, ImmutableClasses.match("abc*", "abXXX"));
    }
    
    @Test
    public void shouldMatchPattern() {
        assertEquals(true, ImmutableClasses.match("abc*", "abc"));
    }
}
