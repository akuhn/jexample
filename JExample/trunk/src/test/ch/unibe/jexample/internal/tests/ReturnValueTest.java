package ch.unibe.jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Stack;

import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.InjectionPolicy;
import ch.unibe.jexample.JExample;
import ch.unibe.jexample.internal.Example;
import ch.unibe.jexample.internal.ExampleGraph;
import ch.unibe.jexample.internal.util.CloneUtil;
import ch.unibe.jexample.internal.util.JExampleError;

public class ReturnValueTest {

    @RunWith(JExample.class)
    private static class WithStackField {

        private Stack<Integer> stack;

        @Test
        public void setUp() {
            stack = new Stack<Integer>();
        }

        @Given("#setUp")
        public void first() {
            assertNotNull(stack);
            assertEquals(0, stack.size());
            stack.push(42);
        }

        @Given("#setUp;#first")
        public void second() {
            assertNotNull(stack);
            assertEquals(0, stack.size());
        }

    }

    @RunWith(JExample.class)
    public static class WithField {
        public String aString;

        @Test
        public void testChangeString() {
            this.aString = "Hello, World";
            assertEquals("Hello, World", this.aString);
        }

        @Given("#testChangeString")
        public void testField() {
            assertEquals("Hello, World", this.aString);
        }
    }

    @RunWith(JExample.class)
    private static class Null {
        @Test
        public Object returnNull() {
            return null;
        }
    }

    @Test
    public void returnValueIsNull() throws JExampleError {
        Example e = runNullExample();

        assertTrue(e.wasSuccessful());
        assertEquals(null, e.getReturnValue().getValue());
    }

    private Example runNullExample() throws JExampleError {
        ExampleGraph egg = new ExampleGraph();
        egg.runJExample(Null.class);
        Example e = egg.findExample(Null.class, "returnNull");
        return e;
    }

    @Test
    public void nullIsCloneable() throws JExampleError {
        Example e = runNullExample();
        assertEquals(null, e.getReturnValue().getValue());
    }

    @Test
    public void changeTestCaseField() throws Exception {
        Example e = runWithField("testChangeString");

        assertNull(e.getReturnValue().getValue());
        assertEquals("Hello, World", CloneUtil.getField(e.getReturnValue().getTestCaseInstance(), "aString"));
    }

    @Test
    public void fieldChanged() throws JExampleError {
        Example e = runWithField("testField");
        assertTrue(e.wasSuccessful());
    }

    private Example runWithField(String example) throws JExampleError {
        ExampleGraph $ = new ExampleGraph();
        $.runJExample(WithField.class);
        Example e = $.findExample(WithField.class, example);
        return e;
    }

    @Test
    public void deepCloneOfTestCase() throws JExampleError {
        Result result = new ExampleGraph().runJExample(WithStackField.class);
        assertTrue(result.wasSuccessful());
    }

}
