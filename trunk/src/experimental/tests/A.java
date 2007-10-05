package experimental.tests;

import org.junit.runner.RunWith;

import experimental.ComposedTestRunner;
import extension.annotations.MyTest;

@RunWith(ComposedTestRunner.class)
public class A {

	public A(){
		
	}
	
	@MyTest
	public void test(){
		
	}
}
