package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import jexample.JExample;
import jexample.internal.Example;
import jexample.internal.ExampleGraph;
import jexample.internal.JExampleError;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;

public class ExceptionExpected {

	@SuppressWarnings("serial")
	static class CustomException extends RuntimeException {
		
	}
	
	@SuppressWarnings("serial")
	static class CustomExceptionSubclass extends CustomException {
		
	}
	
	@RunWith(JExample.class)
	public static class A {
		
		@Test(expected=CustomException.class)
		public void noExceptionPresent() {
			// dont thorw
		}
		
		@Test(expected=CustomException.class)
		public void wrongExceptionPresent() {
			throw new AssertionError();
		}
		
		@Test(expected=CustomException.class)
		public void exceptionPresent() {
			throw new CustomException();
		}

		@Test(expected=CustomException.class)
		public void superclassIsPresent() {
			throw new RuntimeException();
		}

		@Test(expected=CustomException.class)
		public void subclassIsPresent() {
			throw new CustomExceptionSubclass();
		}
		
	}
	
	@Test
	public void textAllExpectCustomException() throws JExampleError {
		ExampleGraph g = new ExampleGraph();
		g.add(A.class);
		assertEquals(5, g.getExamples().size());
		for (Example each : g.getExamples()) {
			assertEquals(CustomException.class, each.expectedException);
		}
	}
	
	@Test
	public void testExceptionPresent() {
		assertEquals(true, Util.runExample(A.class, "exceptionPresent").wasSuccessful());
	}
	
	@Test
	public void testNoExceptionPresent() {
		assertEquals(false, Util.runExample(A.class, "noExceptionPresent").wasSuccessful());
	}
	
	@Test
	public void testWrongExceptionPresent() {
		assertEquals(false, Util.runExample(A.class, "wrongExceptionPresent").wasSuccessful());
	}
	
	@Test
	public void testSuperclassIsPresent() {
		assertEquals(false, Util.runExample(A.class, "superclassIsPresent").wasSuccessful());
	}
	
	@Test
	public void testSubclassIsPresent() {
		assertEquals(true, Util.runExample(A.class, "subclassIsPresent").wasSuccessful());
	}
	
}

