/**
 * 
 */
package jexample.internal.tests;

import java.lang.reflect.Method;

import jexample.JExample;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class ExampleValidateTest {

	private Method stringAsParam;

	private Method twoParams;

	private Method voidReturnType;

	private Method returnsString;

	private Method returnsInt;

	private Method noTestMethod;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.stringAsParam = TestTestClass.class.getMethod( "stringAsParam", java.lang.String.class );
		this.twoParams = TestTestClass.class.getMethod( "twoParams", java.lang.String.class, int.class );
		this.voidReturnType = TestTestClass.class.getMethod( "voidReturnType" );
		this.returnsString = TestTestClass.class.getMethod( "returnsString" );
		this.returnsInt = TestTestClass.class.getMethod( "returnsInt" );
		this.noTestMethod = TestTestClass.class.getMethod( "noTestMethod" );
	}

	@Test
	public void testSameTypes() throws SecurityException, NoSuchMethodException {
//		assertEquals( 0, this.validator.dependencyIsValid( this.stringAsParam, this.returnsString ).size() );
//
//		assertEquals( 0, this.validator.dependencyIsValid( this.twoParams, this.returnsString, this.returnsInt ).size() );
	}

	@Test
	public void testWrongOrder() throws SecurityException, NoSuchMethodException {
//		assertEquals( 2, this.validator.dependencyIsValid( this.twoParams, this.returnsInt, this.returnsString ).size() );
	}

	@Test
	public void testReturnTypeVoid() throws SecurityException, NoSuchMethodException {
//		assertEquals( 0, this.validator.dependencyIsValid( this.returnsString, this.voidReturnType ).size() );
	}

	@Test
	public void testDifferentTypes() throws SecurityException, NoSuchMethodException {
//		assertEquals( 1, this.validator.dependencyIsValid( this.stringAsParam, this.voidReturnType ).size() );
	}

	@Test
	public void testReturnTypeNotVoid() throws SecurityException, NoSuchMethodException {
//		// it's ok, if a method returns something but the dependent method
//		// doesn't use this object
//		assertEquals( 0, this.validator.dependencyIsValid( this.voidReturnType, this.returnsString ).size() );
	}

	@Test
	public void testNumberOfTypes() throws SecurityException, NoSuchMethodException {
//		assertEquals( 1, this.validator.dependencyIsValid( this.stringAsParam, this.returnsString, this.voidReturnType )
//				.size() );
	}

	@Test
	public void testDependencyHasToBeTestMethod() {
//		assertEquals( 1, this.validator.dependencyIsValid( this.returnsInt, this.noTestMethod ).size() );
	}

	@Test
	public void testDependencyCannotBeItself() {
//		assertEquals( 1, this.validator.dependencyIsValid( this.returnsInt, this.returnsInt ).size() );
	}

	@RunWith(JExample.class)
	private class TestTestClass {

		@Test
		public void voidReturnType() {

		}

		@Test
		public String returnsString() {
			return "";
		}

		@Test
		public int returnsInt() {
			return 1;
		}

		@Test
		public void stringAsParam( String string ) {

		}

		@Test
		public void twoParams( String string, int integer ) {

		}

		public void noTestMethod() {

		}
	}
}
