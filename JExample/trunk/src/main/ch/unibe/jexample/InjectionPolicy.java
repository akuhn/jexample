package ch.unibe.jexample;

/** Dependency injection policy.
 * The constants of this enumerated type describe the various policies for
 * injecting cached return values. They are used in conjunction with the
 * {@link Injection} annotation type to specify how the cached return values
 * are cloned before executing a test method.
 *<P>
 * If two or more JExample test methods depend on the same return value,
 * something must be done to avoid side effects. The default policy is to
 * clone values before injection.
 * 
 * @author Adrian Kuhn, 2009
 *
 */
public enum InjectionPolicy {

    /** Uses the {@link Object#clone #clone} method to clone
     * all provided injection values. If cloning fails for a value, its provider is rerun.
     * 
     * @see Cloneable
     * 
     */
    CLONE,
    /** Uses internal reflection to create a field-by-field copy of all provided
     * injection values. This strategy does <i>not</i> call the <tt>#clone</tt>
     * method, and even copies objects whose class does not implement the
     * <tt>Cloneable</tt> interface.
     *<P> 
     * <b>Use with caution,</b> might cause the VM to die without further notice.
     * Please read the source code for further documentation.  
     * 
     */
    DEEPCOPY,
    /** Uses the default injection policy.
     * 
     */
    DEFAULT,
    /** Does not clone any of the provided injection values.
     * The consumer must modify any of the injected arguments (including 
     * field of the containing test class).
     *<P>
     * <b>Use with caution,</b> if the consumer modifies any of the injected values,
     * all side effect are visible to any other consumers of the same return values
     * (even if those other consumers use a safe injection policy!)
     * 
     */
    NONE,
    /** Instead of cloning any cached values, reruns all providers to get fresh values.
     * 
     */
    RERUN;

    public static final String JEXAMPLE_INJECTION = "jexmaple.injection"; // TODO track down users!?
    public static final String JEXAMPLE_RERUN = "jexample.rerun";
    
    
    private static InjectionPolicy RESOLUTION = CLONE;
    
    public static void setDefaultPolicy(InjectionPolicy resolution) {
        if (resolution == DEFAULT) throw new IllegalArgumentException();
        RESOLUTION = resolution;
    }
    
    public InjectionPolicy resolve() {
        if (System.getProperty("jexample.rerun") != null) return RERUN;
        return this == DEFAULT ? RESOLUTION : this;
    }
    
}
