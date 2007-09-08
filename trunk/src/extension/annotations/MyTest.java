package extension.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Lea Haensenberger
 * Date: Sep 7, 2007
 * 
 * <p>The <code>MyTest</code> Annotation is only for testing purposes and will eventually become
 * an Annotation that will allow a test to have dependencies on other tests</p>
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyTest {

}
