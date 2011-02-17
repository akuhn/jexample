package ch.unibe.jexample.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import ch.unibe.jexample.Example;
import ch.unibe.jexample.ExampleGraph;

public class Examples {

	@Test
	public void shouldRunEmptyGraph() {
		ExampleGraph g = new ExampleGraph();
		Result result = new JUnitCore().run(g);
		assertEquals(0,result.getRunCount());
	}
	
	@Test
	public void shouldRunSingle() {
		ExampleGraph g = new ExampleGraph();
		Example example = new Example();
		g.add(example);
		Result result = new JUnitCore().run(g);
		assertEquals(1,result.getRunCount());
	}
	
	@Test
	public void shouldRunThreeSingles() {
		ExampleGraph g = new ExampleGraph();
		g.add(new Example());
		g.add(new Example());
		g.add(new Example());
		Result result = new JUnitCore().run(g);
		assertEquals(3,result.getRunCount());
	}
	
	@Test
	public void shouldRunTriad() {
		ExampleGraph g = new ExampleGraph();
		Example parent = g.add(new Example());
		g.add(new Example(parent));
		g.add(new Example(parent));
		Result result = new JUnitCore().run(g);
		assertEquals(3,result.getRunCount());
		assertEquals(0,result.getFailureCount());
		assertEquals(0,result.getIgnoreCount());
	} 
	
	@Test
	public void shouldIgnoreConsumersOfFailure() {
		ExampleGraph g = new ExampleGraph();
		Example failure = g.add(new Example());
		failure.whenRunThenFail();
		g.add(new Example(failure));
		g.add(new Example(failure));
		Result result = new JUnitCore().run(g);
		assertEquals(3,result.getRunCount());
		assertEquals(1,result.getFailureCount());
		assertEquals(2,result.getIgnoreCount());
	} 
	
	@Test 
	public void shouldRunAllProducers() {
		Example producer = new Example();
		Example producer2 = new Example();
		Example consumer = new Example(producer,producer2);
		assertFalse(producer.hasBeenRun());
		assertFalse(producer2.hasBeenRun());
		assertFalse(consumer.hasBeenRun());
		Result result = new JUnitCore().run(consumer);
		assertEquals(1,result.getRunCount());
		assertEquals(0,result.getFailureCount());
		assertEquals(0,result.getIgnoreCount());
		assertTrue(producer.hasBeenRun());
		assertTrue(producer2.hasBeenRun());
		assertTrue(consumer.hasBeenRun());
	}

	@Test 
	public void shouldIgnoreConsumerOfFailure() {
		Example failure = new Example();
		failure.whenRunThenFail();
		Example consumer = new Example(failure);
		assertFalse(failure.hasBeenRun());
		assertFalse(consumer.hasBeenRun());
		Result result = new JUnitCore().run(consumer);
		assertEquals(1,result.getRunCount());
		assertEquals(0,result.getFailureCount());
		assertEquals(1,result.getIgnoreCount());
		assertTrue(failure.hasBeenRun());
		assertTrue(!consumer.hasBeenRun());
	}
	
	
}
