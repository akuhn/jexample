package experimental.tests;

import org.junit.runner.RunWith;

import experimental.ComposedTestRunner;
import extension.annotations.Depends;
import extension.annotations.MyTest;

@RunWith(ComposedTestRunner.class)
public class B {
	
	public B(){
		
	}

	@MyTest
	public void otherTest(){
		
	}
	
	@MyTest
	@Depends("GraphTest$CyclicOverClasses.depOnOtherTest")
	public void otherTestCyclic(){
		
	}
	
	@MyTest
	@Depends("CycleDetectorTest$WithCycleOverClasses.bottomCyclicMethod")
	public void cyclicMethod(){
		
	}
	
	@MyTest
	@Depends("CycleDetectorTest$WithoutCycleOverClasses.rootMethod")
	public void middleMethod(){
		
	}
}
