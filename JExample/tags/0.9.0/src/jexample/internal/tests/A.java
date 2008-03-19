package jexample.internal.tests;

import jexample.JExampleRunner;
import jexample.Depends;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith( JExampleRunner.class )
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
