package demo;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Stack;

import jexample.Depends;
import jexample.JExample;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JExample.class)
public class StackTest {

	@Test
	public Stack empty() {
		Stack $ = new Stack();
		assertEquals(0, $.size());
		return $;
	}
	
	@Test
	@Depends("#empty")
	public Stack withValue(Stack $) {
		$.push("boe");
		assertEquals(1, $.size());
		return $;
	}
	
	@Test
	@Depends("#withValue")
	public Stack withManyValues(Stack $) {
		$.push("foo");
		$.push("bar");
		assertEquals("bar", $.peek());
		return $;
	}
	
    @Test
    @Depends("#withValue, ListTest#withMoreValues")
    public void testPushAll(Stack $, List l) {
        $.addAll(l);
        assertEquals("Dolor", $.peek());
    }
	

}
