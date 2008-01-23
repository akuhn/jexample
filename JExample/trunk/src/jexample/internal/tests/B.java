package jexample.internal.tests;

import jexample.ComposedTestRunner;
import jexample.Depends;

import org.junit.Test;
import org.junit.runner.RunWith;


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
