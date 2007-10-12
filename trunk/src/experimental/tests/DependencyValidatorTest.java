/**
 * 
 */
package experimental.tests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import extension.DependencyValidator;
import extension.annotations.MyTest;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class DependencyValidatorTest {

	private DependencyValidator validator;

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
		this.validator = new DependencyValidator();

		this.stringAsParam = this.getClass().getMethod( "stringAsParam", java.lang.String.class );
		this.twoParams = this.getClass().getMethod( "twoParams", java.lang.String.class, int.class );
		this.voidReturnType = this.getClass().getMethod( "voidReturnType" );
		this.returnsString = this.getClass().getMethod( "returnsString" );
		this.returnsInt = this.getClass().getMethod( "returnsInt" );
		this.noTestMethod = this.getClass().getMethod( "noTestMethod" );
	}

	@Test
	public void testSameTypes() throws SecurityException, NoSuchMethodException {
		assertEquals( 0, this.validator.dependencyIsValid( this.stringAsParam, this.returnsString ).size() );

		assertEquals( 0, this.validator.dependencyIsValid( this.twoParams, this.returnsString, this.returnsInt ).size() );
	}

	@Test
	public void testWrongOrder() throws SecurityException, NoSuchMethodException {
		assertEquals( 2, this.validator.dependencyIsValid( this.twoParams, this.returnsInt, this.returnsString ).size() );
	}

	@Test
	public void testReturnTypeVoid() throws SecurityException, NoSuchMethodException {
		assertEquals( 0, this.validator.dependencyIsValid( this.returnsString, this.voidReturnType ).size() );
	}

	@Test
	public void testDifferentTypes() throws SecurityException, NoSuchMethodException {
		assertEquals( 1, this.validator.dependencyIsValid( this.stringAsParam, this.voidReturnType ).size() );
	}

	@Test
	public void testReturnTypeNotVoid() throws SecurityException, NoSuchMethodException {
		assertEquals( 1, this.validator.dependencyIsValid( this.voidReturnType, this.returnsString ).size() );
	}

	@Test
	public void testNumberOfTypes() throws SecurityException, NoSuchMethodException {
		assertEquals( 1, this.validator.dependencyIsValid( this.stringAsParam, this.returnsString, this.voidReturnType ).size() );
	}
	
	@Test
	public void testDependencyHasToBeTestMethod(){
		assertEquals( 1, this.validator.dependencyIsValid( this.returnsInt, this.noTestMethod ).size() );
	}
	
	@Test
	public void testDependencyCannotBeItself(){
		assertEquals( 1, this.validator.dependencyIsValid( this.returnsInt, this.returnsInt ).size() );
	}

	@MyTest
	public void voidReturnType() {

	}

	@MyTest
	public String returnsString() {
		return "";
	}

	@MyTest
	public int returnsInt() {
		return 1;
	}

	@MyTest
	public void stringAsParam( String string ) {

	}

	@MyTest
	public void twoParams( String string, int integer ) {

	}
	
	public void noTestMethod(){
		
	}
}
