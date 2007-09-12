import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 */

/**
 * @author Lea Hänsenberger (lhaensenberger at students.unibe.ch)
 */

@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
@Documented
public @interface Depends {

	String value();

}
