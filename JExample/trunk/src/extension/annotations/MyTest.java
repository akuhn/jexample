package extension.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import extension.ComposedTestRunner;

/**
 * <p>
 * The <code>MyTest</code> Annotation marks tests to be run with the {@link ComposedTestRunner}.
 * </p>
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
@Documented
public @interface MyTest {

	/**
	 * Default empty exception
	 */
	static class None extends Throwable {
		private static final long serialVersionUID = 1L;

		private None() {
		}
	}

	/**
	 * Optionally specify <code>expected</code>, a Throwable, to cause a test
	 * method to succeed if an exception of the specified class is thrown by
	 * the method.
	 */
	Class<? extends Throwable> expected() default None.class;

}
