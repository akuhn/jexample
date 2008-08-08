package jexample;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Declares dependencies (and providers) of an example method. Methods with this annotations must be marked as {@link org.junit.Test @Test}, while the declaring class must use
 * <code>{@link org.junit.RunWith @RunWith}({@link jexample.JExample JExample}.class)</code> to select JExample as
 * preferred JUnit runner.
 * <p>
 * When running a test suite, JExample will skips any example method whose dependencies have previously failed or been skipped.
 * Furthermore, dependencies are used for fixture injection, that is, to establish producer-consumer relationships among
 * examples. Therefore, JExample caches the return value of producer methods and injects them as method arguments when a
 * consumer is about to be executed.
 * <p>
 * You can think of example methods as test methods on stereoids. Example methods are, in addition to test methods,
 * written source code to illustrate the usage of the unit under test, and may return a characteristic instance of
 * their unit under test. Thus, test methods are in fact <i>examples</i> of the unit under test.
 * <p>
 * An example method may depend on both successful execution and return value of other examples. If it does, it must declare
 * the dependencies using this annotation. An example methods with dependencies may have method parameters. The number of
 * parameters must be less than or equal to the number of dependencies. The type of the n-th parameter must match the return
 * type of the n-th dependency.
 * <p>
 * Dependency declarations uses the same syntax as the &#64;link tag of the Java documentation tool. References are either
 * fully qualified or not. If less than fully qualified, JExample searches first in the declaring class and then in the
 * enclosing package. The following table shows the different forms of references.
 * <ul>
 * <li>#method</li>
 * <li>#method(Type, Type, ...)</li>
 * <li>class#method</li>
 * <li>class#method(Type, Type, ...)</li>
 * <li>package.class#method</li>
 * <li>package.class#method(Type, Type, ...)</li>
 * </ul>
 *</pre>
 * Multiple references are separated by either a comma (,) or a semicolon (;).
 * <p>
 * <b>NB:</b> As listed above, the hash character (#), rather than a dot (.) separates a member from its class. However, this class is generally
 * lenient and will properly parse a dot if there is no ambiguity. This is the same as the Java documentation tool does.
 * 
 * @author Adrian Kuhn, 2007-2008
 * @author Lea Haensenberger, 2007
 */
@Documented
@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
public @interface Depends {

	String value();

}
