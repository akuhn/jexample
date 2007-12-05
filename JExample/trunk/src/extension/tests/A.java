package extension.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import extension.ComposedTestRunner;
import extension.annotations.Depends;

@RunWith( ComposedTestRunner.class )
public class A {

	public A() {

	}

	@Test
	public void test() {

	}

	@Test
	@Depends( "MethodCollectorTest$ExternalDepsWithCycle.test1" )
	public void test4() {

	}
}
