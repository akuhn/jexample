package experimental.tests;

import org.junit.runner.RunWith;

import experimental.FooRunner;
import extension.annotations.MyTest;

@RunWith(FooRunner.class)
public class B {

	@MyTest
	public void otherTest(){
		
	}
}
