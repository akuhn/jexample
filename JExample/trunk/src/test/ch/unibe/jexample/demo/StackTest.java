package ch.unibe.jexample.demo;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Stack;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
public class StackTest {

    @Test
    public Stack<String> empty() {
        Stack<String> stack = new Stack<String>();
        assertEquals(0, stack.size());
        return stack;
    }

    @Test
    @Given("#empty")
    public Stack<String> withValue(Stack<String> stack) {
        stack.push("boe");
        assertEquals(1, stack.size());
        return stack;
    }

    @Test
    @Given("#withValue")
    public Stack<String> withManyValues(Stack<String> stack) {
        stack.push("foo");
        stack.push("bar");
        assertEquals("bar", stack.peek());
        return stack;
    }

    @Test
    @Given("#withValue, ListTest#withMoreValues")
    public void testPushAll(Stack<String> stack, List<String> list) {
        stack.addAll(list);
        assertEquals("Dolor", stack.peek());
    }

}
