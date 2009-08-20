package ch.unibe.jexample.internal;

import ch.unibe.jexample.InjectionPolicy;
import ch.unibe.jexample.deepclone.CloneFactory;
import ch.unibe.jexample.util.CloneUtil;
import ch.unibe.jexample.util.MethodReference;

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

    public static long NANOS = 0L;

    private static final boolean FORCE_RERUN = System.getProperty("jexample.rerun") != null;
    private Object[] arguments;
    private CloneFactory factory;

    private Object testInstance;
    
    public InjectionValues(Example example) throws Exception {
        this.arguments = new Object[example.method.arity()];
        this.initialize(example.method.getInjectionPolicy(), example);
        this.initializeMissingTestInstance(example.method);
    }
    
    public Object[] getArguments() {
        return arguments;
    }

    public Object getTestInstance() {
        return testInstance;
    }

    private <T> T clone(T object) {
        if (factory == null) factory = new CloneFactory();
        long time = System.nanoTime();
        try {
            return factory.clone(object);
        }
        finally {
            NANOS  += (System.nanoTime() - time);
        }
    }

    private void initialize(InjectionPolicy policy, Example example) throws Exception {
        if (policy == InjectionPolicy.CLONE) this.initializeClone(example);
        else if (policy == InjectionPolicy.DEEPCOPY) this.initializeCopy(example);
        else if (policy == InjectionPolicy.NONE) this.initializeNone(example);
        else if (policy == InjectionPolicy.RERUN) this.initializeRerun(example);
        else this.initializeDefault(example);
    }

    private void initializeClone(Example example) {
        throw new UnsupportedOperationException();
    }

    private void initializeCopy(Example example) {
        this.initializeNone(example);
        arguments = clone(arguments);
        testInstance = clone(testInstance);
    }

    private void initializeDefault(Example example) {
        //this.initializeClone(example);
        this.initializeCopy(example);
    }

    private void initializeMissingTestInstance(MethodReference ref) throws Exception {
        if (!ref.getActualClass().isInstance(testInstance)) testInstance = null;
        if (testInstance == null) {
            testInstance = CloneUtil.getConstructor(ref.getActualClass()).newInstance();
        }
    }
    
    private void initializeNone(Example example) {
        arguments = new Object[example.method.arity()];
        for (int i = 0; i < arguments.length; i++) {
            Example provider = example.node.dependencies().get(i).getProducer().value;
            arguments[i] = provider.getReturnValue().getValue();
            if (i == 0) testInstance = provider.getReturnValue().getTestCaseInstance();
        } 
        if (arguments.length == 0) {
            Example first = example.node.firstProducerOrNull();
            if (first != null) testInstance = first.getReturnValue().getTestCaseInstance();
        }
    }
    
    private void initializeRerun(Example example) throws Exception {
        arguments = new Object[example.method.arity()];
        for (int i = 0; i < arguments.length; i++) {
            Example provider = example.node.dependencies().get(i).getProducer().value;
            ReturnValue returnValue = provider.bareInvoke();
            arguments[i] = returnValue.getValue();
            if (i == 0) testInstance = returnValue.getTestCaseInstance();
        } 
        if (arguments.length == 0) {
            Example first = example.node.firstProducerOrNull();
            if (first != null) testInstance = first.bareInvoke().getTestCaseInstance();
        }
    }
    
}
