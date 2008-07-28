package jexample;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;


/**
 * The <code>Depends</code> Annotation defines the dependencies of a test method.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */

@Documented
@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
public @interface Depends {

	/**
	 * @return a {@link String} representing the {@link Method}'s the declaring {@link Method} depends on.
	 */
	String value();

}
