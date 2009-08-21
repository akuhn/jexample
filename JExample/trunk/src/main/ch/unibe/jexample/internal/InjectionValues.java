package ch.unibe.jexample.internal;

import static ch.unibe.jexample.internal.InjectionStrategy.MISSING;
import ch.unibe.jexample.InjectionPolicy;
import ch.unibe.jexample.internal.util.CloneUtil;

/** Creates injection values for test execution.
 *<P> 
 * The life cycle of instances is limited to the activation (ie method
 * execution) of {@link Example#bareInvoke}. If used that way, instances
 * are stack contended and thus thread-safe. 
 * 
 * @author Adrian Kuhn
 *
 */
public class InjectionValues {

    public static long NANOS = 0L; // XXX for icse paper
    private static final boolean FORCE_RERUN = System.getProperty("jexample.rerun") != null; // XXX for icse paper

    private Object[] arguments;
    private Object testInstance;
    
    public InjectionValues(Object testInstance, Object[] arguments) {
        this.arguments = arguments;
        this.testInstance = testInstance;
    }
    
    public static InjectionValues make(Example example) throws Exception {
        if (example.node.dependencies().isEmpty()) 
            return new InjectionValues(fixReceiver(example, null), new Object[] {});
        Object receiver = null; // pending
        Object[] arguments = new Object[example.method.arity()];
        InjectionPolicy policy = example.method.getInjectionPolicy();
        InjectionStrategy strategy = pickStrategy(policy);
        receiver = example.node.dependencies().get(0).getProducer().value.getReturnValue().getTestCaseInstance();
        for (int n = 0; n < arguments.length; n++) {
            arguments[n] = example.node.dependencies().get(n).getProducer().value.getReturnValue().getValue(); // FIXME Demeter!?
        }
        receiver = fixReceiver(example, receiver);
        return strategy.makeInjectionValues(receiver, arguments).rerunMissingValues(example);
    }

    private InjectionValues rerunMissingValues(Example example) throws Exception {
        if (testInstance == MISSING ||
                (arguments.length > 0 && arguments[0] == MISSING)) {
            ReturnValue value = example.node.dependencies().get(0).getProducer().value.bareInvoke();
            testInstance = value.getTestCaseInstance();
            if (arguments.length > 0 && arguments[0] == MISSING) arguments[0] = value.getValue();
        }
        for (int n = 1; n < arguments.length; n++) {
            if (arguments[n] != MISSING) continue;
            arguments[n] = example.node.dependencies().get(n).getProducer().value.bareInvoke().getValue(); // FIXME Demeter!?
        }
        return this;
    }

    private static Object fixReceiver(Example example, Object receiver) throws Exception {
        Object result = example.method.getActualClass().isInstance(receiver) ? receiver : null;
        if (result == null) result = CloneUtil.getConstructor(example.method.getActualClass()).newInstance();
        return result;        
    }

    private static InjectionStrategy pickStrategy(InjectionPolicy policy) {
        if (FORCE_RERUN) return new RerunInjectionStrategy();
        if (policy == InjectionPolicy.CLONE) return new CloneInjectionStrategy();
        if (policy == InjectionPolicy.DEEPCOPY) return new DeepcopyInjectionStrategy();
        if (policy == InjectionPolicy.NONE) return new NoneInjectionStrategy();
        if (policy == InjectionPolicy.RERUN) return new RerunInjectionStrategy();
        return new DeepcopyInjectionStrategy(); // use clone instead
    }
     
    public Object[] getArguments() {
        return arguments;
    }

    public Object getTestInstance() {
        return testInstance;
    }
    
}
