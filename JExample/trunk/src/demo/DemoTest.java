package demo;

import static org.junit.Assert.assertEquals;
import jexample.Depends;
import jexample.JExampleRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JExampleRunner.class)
public class DemoTest {

	@Test
	public Demo demoTest(){
		Demo aDemo = new Demo("Hi, I'm a Demo.");
		assertEquals(aDemo, aDemo);
		
		System.out.println("demoTest:\t" + aDemo);
		
		return aDemo;
	}
	
	@Test
	@Depends("demoTest")
	public void secondDemoTest(Demo clonedDemo){
		assertEquals(new Demo("Hi, I'm a Demo."), clonedDemo);
		
		System.out.println("secondDemoTest:\t" + clonedDemo);
	}
}
