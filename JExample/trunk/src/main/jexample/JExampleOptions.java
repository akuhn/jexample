package jexample;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Adrian Kuhn
 *
 */

@Documented
@JExampleOptions
@Target( { ElementType.TYPE } )
@Retention( RetentionPolicy.RUNTIME )
public @interface JExampleOptions {

    public boolean cloneReturnValues() default true; 
    
    public boolean cloneTestCase() default false;
    
}
