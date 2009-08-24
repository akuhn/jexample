package ch.unibe.jexample;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Declares how to clone the previously cached return values
 * when running a consuming test method.
 *<P>
 *  
 * @author Adrian Kuhn, 2009
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Injection {

    InjectionPolicy value() default InjectionPolicy.DEFAULT;
    
}
