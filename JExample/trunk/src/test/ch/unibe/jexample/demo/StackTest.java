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
    public Stack<String> emptyStack() {
        Stack<String> stack = new Stack<String>();
        assertEquals(0, stack.size());
        return stack;
    }

    @Test
    @Given("#emptyStack")
    public Stack<String> shouldPush(Stack<String> stack) {
        stack.push("boe");
        assertEquals(1, stack.size());
        return stack;
    }
    
    @Test
    @Given("#shouldPush") 
    public Stack<String> withValue(Stack<String> stack) {
        return stack;
    }

    @Test
    @Given("#shouldPush")
    public void shouldPop(Stack<String> stack) {
        int size = stack.size();
        String string = stack.pop();
        assertEquals("boe", string);
        assertEquals(size - 1, stack.size());
    }
    
    @Test
    @Given("#shouldPush")
    public Stack<String> withManyValues(Stack<String> stack) {
        stack.push("foo");
        stack.push("bar");
        assertEquals("bar", stack.peek());
        return stack;
    }

    @Test
    @Given("#withValue;ListTest#withMoreValues")
    public void testPushAll(Stack<String> stack, List<String> list) {
        stack.addAll(list);
        assertEquals("Dolor", stack.peek());
    }

}
