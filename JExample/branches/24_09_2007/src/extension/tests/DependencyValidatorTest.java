/**
 * 
 */
package extension.tests;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;

import extension.DependencyValidator;

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

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.validator = new DependencyValidator();
		
		this.stringAsParam = this.getClass().getMethod( "stringAsParam", java.lang.String.class );
		this.twoParams = this.getClass().getMethod( "twoParams", java.lang.String.class, java.lang.Integer.class );
		this.voidReturnType = this.getClass().getMethod( "voidReturnType");
		this.returnsString = this.getClass().getMethod( "returnsString" );
		this.returnsInt = this.getClass().getMethod( "returnsInt" );
	}

	@Test
	public void testSameTypes() throws SecurityException, NoSuchMethodException, InitializationError {
		assertTrue( this.validator.dependencyIsValid( this.stringAsParam, this.returnsString ) );

		assertTrue( this.validator.dependencyIsValid( this.twoParams, this.returnsString, this.returnsInt ) );
	}

	@Test( expected = InitializationError.class )
	public void testWrongOrder() throws SecurityException, InitializationError, NoSuchMethodException {
		this.validator.dependencyIsValid( this.twoParams, this.returnsInt, this.returnsString );
	}

	@Test
	public void testReturnTypeVoid() throws SecurityException, NoSuchMethodException, InitializationError {
		assertTrue( this.validator.dependencyIsValid( this.returnsString, this.voidReturnType ) );
	}

	@Test( expected = InitializationError.class )
	public void testDifferentTypes() throws SecurityException, NoSuchMethodException, InitializationError {
		this.validator.dependencyIsValid( this.stringAsParam, this.voidReturnType );
	}

	@Test( expected = InitializationError.class )
	public void testReturnTypeNotVoid() throws SecurityException, NoSuchMethodException, InitializationError {
		this.validator.dependencyIsValid( this.voidReturnType, this.returnsString );
	}

	@Test( expected = InitializationError.class )
	public void testNumberOfTypes() throws SecurityException, InitializationError, NoSuchMethodException {
		this.validator.dependencyIsValid( this.stringAsParam, this.returnsString, this.voidReturnType );
	}

	public void voidReturnType() {

	}

	public String returnsString() {
		return "";
	}

	public Integer returnsInt() {
		return 1;
	}

	public void stringAsParam( String string ) {

	}

	public void twoParams( String string, Integer integer ) {

	}
}
