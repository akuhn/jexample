package ch.unibe.jexample;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares dependencies (and providers) of an example method. When running a
 * test suite, JExample will skips any example method whose dependencies have
 * previously failed or been skipped. Furthermore, dependencies are used for
 * fixture injection, that is, to establish producer-consumer relationships
 * among examples. Therefore, JExample caches the return value of producer
 * methods and injects them as method arguments when a consumer is about to be
 * executed.
 * <p>
 * You can run both JUnit and JExample tests in the same test suite, even using
 * the JUnit plugin of Eclipse. All example methods must be marked as
 * {@link org.junit.Test @Test} and the declaring class must use
 * {@link org.junit.RunWith @RunWith} to select
 * {@link ch.unibe.jexample.JExample JExample} as its preferred test runner.
 * <p>
 * You can think of example methods as test methods on stereoids. They do more
 * than just testing the unit under test. Example methods are written source
 * code to illustrate the usage of the unit under test, and may return a
 * characteristic instance of their unit under test. Thus, examples methods are
 * in fact <i>examples</i> of the unit under test.
 * <p>
 * As such, example methods tackle the same problem as mocks. How to test a unit
 * which depends on other units? When working with mocks, you solve this problem
 * by creating a mock for each dependency. When working with examples, you solve
 * this problem by declaring producer-consumer dependencies. Thus, instead of
 * testing against mocks, you test against the previously created return values
 * of other tests. Since example methods are both producers and testers of their
 * returned value, all return values are guaranteed to be valid and fully
 * functional instances of the corresponding unit. In addition, JExample will
 * use cloning to take care that no side-effects are introduced when two or more
 * consumers use the same return value.
 * <p>
 * An example method may depend on both successful execution and return value of
 * other examples. If it does, it must declare the dependencies using this
 * annotation. An example methods with dependencies may have method parameters.
 * The number of parameters must be less than or equal to the number of
 * dependencies. The type of the n-th parameter must match the return type of
 * the n-th dependency.
 * <p>
 * Dependency declarations uses the same syntax as the &#64;link tag of the Java
 * documentation tool. References are either fully qualified or not. If less
 * than fully qualified, JExample searches first in the declaring class and then
 * in the enclosing package. The following table shows the different forms of
 * references.
 * <ul>
 * <li>#method</li>
 * <li>#method(Type, Type, ...)</li>
 * <li>class#method</li>
 * <li>class#method(Type, Type, ...)</li>
 * <li>package.class#method</li>
 * <li>package.class#method(Type, Type, ...)</li>
 * </ul>
 * </pre> Multiple references are separated by either a comma (,) or a semicolon
 * (;).
 * <p>
 * <b>NB:</b> As listed above, the hash character (#), rather than a dot (.)
 * separates a member from its class. However, this class is generally lenient
 * and will properly parse a dot if there is no ambiguity. This is the same as
 * the Java documentation tool does.
 * 
 * @author Markus Gaelli, 2009
 * @author Lea Haensenberger, 2007
 * @author Adrian Kuhn, 2007-2009
 * 
 * @see http
 *      ://www.iam.unibe.ch/~scg/Research/JExample/ch.unibe.jexample-r246.jar
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Given {

    String value();

}
