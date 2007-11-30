package extension.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 * 
 * 
 * <p>
 * The <code>MyTest</code> Annotation is only for testing purposes and will
 * eventually become an Annotation that will allow a test to have dependencies
 * on other tests
 * </p>
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
	 * method to succeed iff an exception of the specified class is thrown by
	 * the method.
	 */
	Class<? extends Throwable> expected() default None.class;

}
