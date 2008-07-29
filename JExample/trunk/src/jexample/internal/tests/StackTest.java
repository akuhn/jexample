package jexample.internal.tests;

import static org.junit.Assert.assertEquals;

import java.util.Stack;

import jexample.Depends;
import jexample.JExampleRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JExampleRunner.class)
public class StackTest {

	@Test
	public Stack empty() {
		Stack stack = new Stack();
		assertEquals(0, stack.size());
		return stack;
	}
	
	@Test
	@Depends("empty")
	public Stack withValue(Stack stack) {
		stack.push("boe");
		assertEquals(1, stack.size());
		return stack;
	}
	
	@Test
	@Depends("withValue")
	public Stack withManyValues(Stack stack) {
		stack.push("foo");
		stack.push("bar");
		assertEquals("bar", stack.peek());
		return stack;
	}

}
