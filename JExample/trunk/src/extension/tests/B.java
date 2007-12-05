package extension.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import extension.ComposedTestRunner;
import extension.annotations.Depends;

@RunWith( ComposedTestRunner.class )
public class B {

	public B() {

	}

	@Test
	public void otherTest() {

	}

	@Test
	@Depends( "GraphTest$CyclicOverClasses.depOnOtherTest" )
	public void otherTestCyclic() {

	}

	@Test
	@Depends( "CycleDetectorTest$WithCycleOverClasses.bottomCyclicMethod" )
	public void cyclicMethod() {

	}

	@Test
	@Depends( "CycleDetectorTest$WithoutCycleOverClasses.rootMethod" )
	public void middleMethod() {

	}
}
