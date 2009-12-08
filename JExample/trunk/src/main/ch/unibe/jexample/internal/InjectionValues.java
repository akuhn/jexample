package ch.unibe.jexample.internal;

import static ch.unibe.jexample.InjectionPolicy.CLONE;
import static ch.unibe.jexample.InjectionPolicy.DEEPCOPY;
import static ch.unibe.jexample.InjectionPolicy.NONE;
import static ch.unibe.jexample.InjectionPolicy.RERUN;
import static ch.unibe.jexample.internal.InjectionStrategy.MISSING;

import java.util.List;

import ch.unibe.jexample.InjectionPolicy;
import ch.unibe.jexample.internal.graph.Edge;
import ch.unibe.jexample.internal.util.Reflection;

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

    private Object[] arguments;
    private Object testInstance;
    
    public InjectionValues(Object testInstance, Object[] arguments) {
        this.arguments = arguments;
        this.testInstance = testInstance;
    }
    
    public static InjectionValues make(Example example) throws Exception {
        if (example.producers().isEmpty()) 
            return new InjectionValues(fixReceiver(example, null), new Object[] {});
        if (hasLinearDependenciesOnly(example)) {
            Object[] arguments = new Object[example.method.arity()];
            for (int n = 0; n < arguments.length; n++) {
                arguments[n] = example.producers().get(n).getReturnValue().getValue(); // FIXME Demeter!?
            }
            return new InjectionValues(fixReceiver(example, null), arguments);
        }
        Object[] arguments = new Object[example.method.arity()];
        InjectionPolicy policy = example.method.getInjectionPolicy();
        InjectionStrategy strategy = pickStrategy(policy);
        Object receiver = example.producers().get(0).getReturnValue().getTestCaseInstance();
        for (int n = 0; n < arguments.length; n++) {
            arguments[n] = example.producers().get(n).getReturnValue().getValue(); // FIXME Demeter!?
        }
        receiver = fixReceiver(example, receiver);
        return strategy.makeInjectionValues(receiver, arguments).rerunMissingValues(example);
    }

    private static boolean hasLinearDependenciesOnly(Example example) {
        return false;
//        for (Example current = example;;) {
//            if (current.consumers().size() > 1) return false;
//            if (current.consumers().size() == 0) break;
//            current = current.consumers().iterator().next();
//        }
//        for (int n = 0; n < example.method.arity(); n++) {
//            if (example.producers().get(n).consumers().size() > 1) return false;
//        }
//        return true;
    }

    private InjectionValues rerunMissingValues(Example example) throws Exception {
        if (testInstance == MISSING ||
                (arguments.length > 0 && arguments[0] == MISSING)) {
            ReturnValue value = example.producers().get(0).getReturnValueAndDispose();
            testInstance = value.getTestCaseInstance();
            if (arguments.length > 0 && arguments[0] == MISSING) arguments[0] = value.getValue();
        }
        for (int n = 1; n < arguments.length; n++) {
            if (arguments[n] != MISSING) continue;
            arguments[n] = example.producers().get(n).getReturnValueAndDispose().getValue(); // FIXME Demeter!?
        }
        return this;
    }

    private static Object fixReceiver(Example example, Object receiver) throws Exception {
        Object result = example.method.getActualClass().isInstance(receiver) ? receiver : null;
        if (result == null) result = Reflection.newInstance(example.method.getActualClass());
        return result;        
    }

    private static InjectionStrategy pickStrategy(InjectionPolicy policy) {
        InjectionPolicy resolution = policy.resolve();
        if (resolution == CLONE) return new CloneInjectionStrategy();
        if (resolution == DEEPCOPY) return new DeepcopyInjectionStrategy();
        if (resolution == NONE) return new NoneInjectionStrategy();
        if (resolution == RERUN) return new RerunInjectionStrategy();
        throw new AssertionError();
    }
     
    public Object[] getArguments() {
        return arguments;
    }

    public Object getReceiver() {
        return testInstance;
    }
    
}
