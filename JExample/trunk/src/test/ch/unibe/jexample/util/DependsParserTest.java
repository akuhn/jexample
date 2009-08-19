/**
 * 
 */
package ch.unibe.jexample.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;
import ch.unibe.jexample.util.MethodLocator;
import ch.unibe.jexample.util.MethodReference;

/**
 * @author Lea Haensenberger
 * @author Adrian Kuhn
 * 
 */
public class DependsParserTest {

	@RunWith(JExample.class)
	static class A {
		@Test
		public void unique() { /**/ }

		@Test
		public void test() { /**/ }

		@Test
		public void test(String s) { /**/ }

		@Test
		public void test(int x) { /**/ }

		@Test
		public void test(long x) { /**/ }

		@Test
		public void test(double x) { /**/ }

		@Test
		public void test(float x) { /**/ }

		@Test
		public void test(boolean x) { /**/ }

		@Test
		public void test(char x) { /**/ }
	}

	@Test
	public void uniqueSimpleName() throws Exception {
		MethodLocator loc = MethodLocator.parse("unique");
		MethodReference ref = loc.resolve(A.class);
		assertEquals(A.class, ref.getActualClass());
		assertEquals("unique", ref.getName());
	}

	@Test
	public void ambigousSimpleName() throws Exception {
		MethodReference ref = MethodLocator.parse("test").resolve(A.class);
		assertTrue(ref.isBroken());
		assertTrue(ref.getError() instanceof NoSuchMethodException);
		assertTrue(ref.getError().getMessage().startsWith("Ambigous depedency"));
	}

	@Test
	public void testWithoutParameters() throws Exception {
		MethodReference ref = MethodLocator.parse("test()").resolve(A.class);
		assertEquals(A.class, ref.getActualClass());
		assertEquals("test", ref.getName());
		assertEquals(0, ref.getParameterTypes().length);
	}

	@Test
	public void testWithString() throws Exception {
		MethodReference ref = MethodLocator.parse("test(String)").resolve(A.class);
		assertEquals(A.class, ref.getActualClass());
		assertEquals("test", ref.getName());
		assertEquals(1, ref.getParameterTypes().length);
		assertEquals(String.class, ref.getParameterTypes()[0]);
	}

	@Test
	public void testWithInt() throws Exception {
		MethodReference ref = MethodLocator.parse("test(int)").resolve(A.class);
		assertEquals(A.class, ref.getActualClass());
		assertEquals("test", ref.getName());
		assertEquals(1, ref.getParameterTypes().length);
		assertEquals(int.class, ref.getParameterTypes()[0]);
	}

	@Test
	public void testWithLong() throws Exception {
		MethodReference ref = MethodLocator.parse("test(long)").resolve(A.class);
		assertEquals(A.class, ref.getActualClass());
		assertEquals("test", ref.getName());
		assertEquals(1, ref.getParameterTypes().length);
		assertEquals(long.class, ref.getParameterTypes()[0]);
	}

	@Test
	public void testWithFloat() throws Exception {
		MethodReference ref = MethodLocator.parse("test(float)").resolve(A.class);
		assertEquals(A.class, ref.getActualClass());
		assertEquals("test", ref.getName());
		assertEquals(1, ref.getParameterTypes().length);
		assertEquals(float.class, ref.getParameterTypes()[0]);
	}

	@Test
	public void testWithDouble() throws Exception {
		MethodReference ref = MethodLocator.parse("test(double)").resolve(A.class);
		assertEquals(A.class, ref.getActualClass());
		assertEquals("test", ref.getName());
		assertEquals(1, ref.getParameterTypes().length);
		assertEquals(double.class, ref.getParameterTypes()[0]);
	}

	@Test
	public void testWithChar() throws Exception {
		MethodReference ref = MethodLocator.parse("test(char)").resolve(A.class);
		assertEquals(A.class, ref.getActualClass());
		assertEquals("test", ref.getName());
		assertEquals(1, ref.getParameterTypes().length);
		assertEquals(char.class, ref.getParameterTypes()[0]);
	}

	@Test
	public void testWithBoolean() throws Exception {
		MethodReference ref = MethodLocator.parse("test(boolean)").resolve(A.class);
		assertEquals(A.class, ref.getActualClass());
		assertEquals("test", ref.getName());
		assertEquals(1, ref.getParameterTypes().length);
		assertEquals(boolean.class, ref.getParameterTypes()[0]);
	}

	@Test
	public void packageLookup() throws Exception {
		MethodReference ref = MethodLocator.parse("DependsParserTest$A.unique").resolve(Arrays.class);
		assertEquals(A.class, ref.getActualClass());
		assertEquals("unique", ref.getName());
	}

	@Test
	public void innerclassLookup() throws Exception {
		MethodReference ref = MethodLocator.parse("DependsParserTest$A.unique").resolve(DependsParserTest.class);
		assertEquals(A.class, ref.getActualClass());
		assertEquals("unique", ref.getName());
	}

	@Test
	public void qualifiedLookup() throws Exception {
		MethodReference ref = MethodLocator.parse("ch.unibe.jexample.util.DependsParserTest$A.unique")
		.resolve(Void.class);
		assertEquals(A.class, ref.getActualClass());
		assertEquals("unique", ref.getName());
	}

	public void classNotFound() throws Exception {
		MethodReference ref = MethodLocator.parse("Zork.method").resolve(A.class);
		assertTrue(ref.isBroken());
	}

	public void noSuchMethod() throws Exception {
		MethodReference ref = MethodLocator.parse("zork()").resolve(A.class);
		assertTrue(ref.isBroken());
	}

	public void noSuchUniqueMethod() throws Exception {
		MethodReference ref = MethodLocator.parse("zork").resolve(A.class);
		assertTrue(ref.isBroken());
	}

	public void parameterTypeNotFound() throws Exception {
		MethodReference ref = MethodLocator.parse("test(zork)").resolve(A.class);
		assertTrue(ref.isBroken());
	}

	@RunWith(JExample.class)
	static class B {

		@Test
		public void otherTest() { /**/ }

		@Test
		@Given("GraphTest$CyclicOverClasses.depOnOtherTest")
		public void otherTestCyclic() { /**/ }

		@Test
		@Given("CycleDetectorTest$WithCycleOverClasses.bottomCyclicMethod")
		public void cyclicMethod() { /**/ }

		@Test
		@Given("CycleDetectorTest$WithoutCycleOverClasses.rootMethod")
		public void middleMethod() { /**/ }
		
	}

}
