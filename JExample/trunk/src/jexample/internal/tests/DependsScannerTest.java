package jexample.internal.tests;

import static jexample.internal.DependsScanner.scan;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import jexample.internal.DependsScanner.Token;

import org.junit.Test;

public class DependsScannerTest {

	@Test
	public void simpleName() {
		Token[] tokens = scan("method");
		assertEquals(1, tokens.length);
		assertNull(tokens[0].className);
		assertEquals("method", tokens[0].methodName);
		assertNull(tokens[0].parameterNames);
	}

	@Test
	public void fullName() {
		Token[] tokens = scan("ch.akuhn.package.Class.method");
		assertEquals(1, tokens.length);
		assertEquals("ch.akuhn.package.Class", tokens[0].className);
		assertEquals("method", tokens[0].methodName);
		assertNull(tokens[0].parameterNames);
	}

	@Test
	public void emptyParams() {
		Token[] tokens = scan("method()");
		assertEquals(1, tokens.length);
		assertNull(tokens[0].className);
		assertEquals("method", tokens[0].methodName);
		assertNotNull(tokens[0].parameterNames);
		assertEquals(0, tokens[0].parameterNames.length);
	}

	@Test
	public void oneParam() {
		Token[] tokens = scan("method(int)");
		assertEquals(1, tokens.length);
		assertNull(tokens[0].className);
		assertEquals("method", tokens[0].methodName);
		assertArrayEquals("int".split(","), tokens[0].parameterNames);
	}
	
	@Test
	public void manyParam() {
		Token[] tokens = scan("method(int,String,List)");
		assertEquals(1, tokens.length);
		assertNull(tokens[0].className);
		assertEquals("method", tokens[0].methodName);
		assertArrayEquals("int,String,List".split(","), tokens[0].parameterNames);
	}
	
	@Test
	public void fullParam() {
		Token[] tokens = scan("method(int,java.lang.String,java.util.List)");
		assertEquals(1, tokens.length);
		assertNull(tokens[0].className);
		assertEquals("method", tokens[0].methodName);
		assertArrayEquals("int,java.lang.String,java.util.List".split(","), tokens[0].parameterNames);
	}
	
	
}
