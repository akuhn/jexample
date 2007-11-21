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
 * <p>The {@link DependsAbove} Annotation is used to define a dependency to the
 * testmethod that is implemented in the java-File right before the method annotated with {@link DependsAbove}</p>
 */

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
@Documented
public @interface DependsAbove {

}
