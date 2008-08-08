package jexample;

import jexample.internal.TestClass;

import org.junit.internal.runners.InitializationError;

public class JExampleRunner extends JExample {

	public JExampleRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
	}
	
	public JExampleRunner(TestClass testCase) {
	    super(testCase);
	}

}
