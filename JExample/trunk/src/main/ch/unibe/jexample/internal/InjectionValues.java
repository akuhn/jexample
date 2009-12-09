package ch.unibe.jexample.internal;

import static ch.unibe.jexample.internal.ReturnValue.MISSING;

/** Creates injection values for test execution.
 *<P> 
 * The life cycle of instances is limited to the activation (ie method
 * execution) of {@link Example#bareInvoke}.
 * 
 * @author Adrian Kuhn
 *
 */
public class InjectionValues {

    private Object[] arguments;
    private Object receiver;
    
    protected InjectionValues(Object receiver, Object[] arguments) {
        this.arguments = arguments;
        this.receiver = receiver;
    }
    
    /** Create injection values for specified example.
     * 
     */
    public static InjectionValues make(Example example) throws Exception {
        Object receiver = example.fetchReceiver();
        if (example.producers().isEmpty()) return new InjectionValues(receiver, new Object[] {});
        InjectionStrategy strategy = example.resolveInjectionStrategy();
        Object[] arguments = example.fetchArguments();
        return strategy
            .cloneInjectionValues(receiver, arguments)
            .fetchMissingValues(example);
    }

    /** In this method, we fetch the original of all values that had not been
     * cloneable and flush the cache from where they been taken. This ensures
     * that the provider of the original value is rerun upon the next request
     * for its return value. It is thus safe to use the original value.
     * 
     */
    private InjectionValues fetchMissingValues(Example example) throws Exception {
        // first producer provides of both receiver and first argument !!
        boolean firstAgrumentMissing = (arguments.length > 0 && arguments[0] == MISSING);
        if (firstAgrumentMissing || receiver == MISSING) {
            ReturnValue value = example.producers().first().fetchReturnValueAndFlush();
            receiver = value.getTestCaseInstance();
            if (firstAgrumentMissing) arguments[0] = value.getValue();
        }
        // remaining producer provide arguments only
        for (int n = 1; n < arguments.length; n++) {
            if (arguments[n] != MISSING) continue;
            arguments[n] = example.producers().get(n).fetchReturnValueAndFlush().getValue();
        }
        return this;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public Object getReceiver() {
        return receiver;
    }
    
}
