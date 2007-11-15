package extension.tests;

import org.junit.runner.RunWith;

import extension.ComposedTestRunner;
import extension.annotations.MyTest;

@RunWith(ComposedTestRunner.class)
public class A {

	public A(){
		
	}
	
	@MyTest
	public void test(){
		
	}
}
