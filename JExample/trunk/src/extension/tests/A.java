package extension.tests;

import org.junit.runner.RunWith;

import extension.ComposedTestRunner;
import extension.annotations.Depends;
import extension.annotations.MyTest;

@RunWith(ComposedTestRunner.class)
public class A {

	public A(){
		
	}
	
	@MyTest
	public void test(){
		
	}
	
	@MyTest
	@Depends("MethodCollectorTest$ExternalDepsWithCycle.test1")
	public void test4(){
		
	}
}
