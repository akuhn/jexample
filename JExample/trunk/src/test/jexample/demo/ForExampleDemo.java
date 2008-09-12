package jexample.demo;

import java.util.Stack;

import jexample.For;

public class ForExampleDemo {

	public static void main(String... args) {

	    Stack stack = For.example(StackTest.class, "withManyValues");
		
	    System.out.println(stack);
		
	}
	
}
