package ch.unibe.jexample.test;

import static org.junit.Assert.*;

import java.util.Stack;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JExample.class)
public class StackExample {

	@SuppressWarnings("serial")
	private class S extends Stack<String> {};
	
	@Test
	public S shouldBeEmpty() {
		S stack = new S();
		assertEquals(0, stack.size());
		return stack;
	}
	
	@Given("shouldBeEmpty")
	public S shouldAddElement(S stack) {
		stack.add("Element");
		assertEquals(1, stack.size());
		return stack;
	}
	
}
