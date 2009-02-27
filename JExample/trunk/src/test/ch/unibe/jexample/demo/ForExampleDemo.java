package ch.unibe.jexample.demo;

import java.util.Stack;

import ch.unibe.jexample.For;


public class ForExampleDemo {

	public static void main(String... args) {

	    Stack stack = For.example(StackTest.class, "withManyValues");
		
	    System.out.println(stack);
		
	}
	
}
