package ch.unibe.jexample;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Declares how to clone the previously cached return values
 * when running a consuming test method.
 * If no Injection annotation is present on a JExample test class,
 * the injection policy defaults to {@link InjectionPolicy.DEFAULT}. 
 *  
 * @author Adrian Kuhn, 2009
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Injection {

    InjectionPolicy value() default InjectionPolicy.DEFAULT;
    
}
