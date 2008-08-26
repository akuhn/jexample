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
@Target( { ElementType.TYPE } )
@Retention( RetentionPolicy.RUNTIME )
public @interface InjectionPolicy {

    public boolean keep() default false;
    
}
