package ch.unibe.jexample.internal;

import java.util.Iterator;

import ch.unibe.jexample.JExampleOptions;
import ch.unibe.jexample.deepclone.CloneFactory;
import ch.unibe.jexample.internal.graph.Edge;
import ch.unibe.jexample.util.CloneUtil;

/** Creates injection values for test execution.
 *<P> 
 * The life cycle of instances is limited to the activation (ie method
 * execution) of {@link Example#bareInvoke}. If used that way, instances
 * are stack contained and thus thread-safe. 
 * 
 * @author Adrian Kuhn
 *
 */
public class InjectionValues {

    private static final boolean FORCE_RERUN = System.getProperty("jexample.rerun") != null;
    
    
    
    private Object testInstance;
    private Object[] arguments;



    private CloneFactory factory;
    
    public InjectionValues(Example example) throws Exception {
        this.arguments = new Object[example.method.arity()];
        this.initialize(example);
    }
    
    private Void initialize(Example example) throws Exception {
        if (FORCE_RERUN) return forceRerun(example);
        Iterator<Edge<Example>> it = example.node.dependencies().iterator();
        for (int i = 0; i < arguments.length; i++) {
            ReturnValue r = it.next().getProducer().value.getReturnValue();
            arguments[i] = adaptArgument(example.policy, r.getValue());
        }
        testInstance = adaptReceiver(example);
        return null;
    }
    
    private Object adaptReceiver(Example example) throws Exception {
        Example first = example.node.firstProducerOrNull();
        if (example.policy.cloneTestCase() && first != null 
                && first.getReturnValue().isTestCaseInstanceOf(example.method.getActualClass())) {
            return clone(first.getReturnValue().getTestCaseInstance());
        }
        else {
            return CloneUtil.getConstructor(example.method.getActualClass()).newInstance();
        }
    }

    private Object adaptArgument(JExampleOptions policy, Object value) {
        if (!policy.cloneReturnValues()) return value;
        return clone(value);
    }

    private Void forceRerun(Example example) throws Exception {
        int length = example.method.arity();
        for (int i = 0; i < length; i++) {
            Example each = example.node.dependencies().get(i).getProducer().value;
            each.bareInvoke();
            arguments[i] = each.getReturnValue().getValue();
        }
        testInstance = null;
        Example first = example.node.firstProducerOrNull();
        if (first != null && first.getReturnValue().isTestCaseInstanceOf(
                    example.method.getActualClass())) {
            if (length == 0) first.bareInvoke(); // loop above was never passed
            testInstance = first.getReturnValue().getTestCaseInstance();
        }
        else {
            testInstance = CloneUtil.getConstructor(example.method.getActualClass()).newInstance();
        }
        return null;
    }

    public Object getTestInstance() {
        return testInstance;
    }
    
    public Object[] getArguments() {
        return arguments;
    }
    
    private Object clone(Object object) {
        if (factory == null) factory = new CloneFactory();
        return factory.clone(object);
    }
    
}
