package ch.unibe.jexample;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Declares options for JExample test-classes. 
 * The options provided by this annotations let you configure how values are
 * passed from test method to test method. For each executed test-method, the
 * produced return value is cached. When another method requests such a cached
 * return value, the <code>JExampleOptions</code> annotation of its containing
 * class decides how the return value is passed to the consumer.
 *   
 * @author Adrian Kuhn, 2008
 *
 */

@Documented
@JExampleOptions
@Target( { ElementType.TYPE } )
@Retention( RetentionPolicy.RUNTIME )
public @interface JExampleOptions {

    /** Controls how return values are passed from producer to consumer.
     * <p>
     * If this option is <code>true</code> (default) are cloned before passing
     * them along. If the value is not cloneable, the procuder test is rerun in
     * order to get a new return value. A value is considered to be cloneable
     * iff its class a) implements the {@link Cloneable} interface <i>and</i> b)
     * override {@link Object#clone} with a public method. Any other value is
     * considered to be uncloneable. Immutable objects are not cloned, even when they are cloneable. At the moment,
     * the set of immutable objects is limited to instances of {@link String} and
     * boxed primitives. In the future (see JSR 308) this might change.
     * <p>
     * If this option is <code>false</code> return values are passed along without
     * cloning or rerunning. If the same return value is used by more than one
     * consumer without caution, side-effects might appear. Such side-effect are
     * hard to track down (see Heisenbug), thus use this setting only if you 
     * know what you are doing.
     * 
     * @return defaults to <code>true</code>.
     */
    public boolean cloneReturnValues() default true; 
    
    /** Controls whether the enclosing instance of a test case is cached or not.
     * Please do not use this setting in new JExample code! This setting is
     * offered for legacy purposes only.
     * 
     * More details see API Migration plugin (under development).
     * 
     * @return defaults to <code>false</code>.
     */
    public boolean cloneTestCase() default false;
    
}
