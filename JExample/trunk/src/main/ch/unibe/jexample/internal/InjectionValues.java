package ch.unibe.jexample.internal;

import ch.unibe.jexample.util.CloneUtil;

public class InjectionValues {

    private static final boolean FORCE_RERUN = System.getProperty("jexample.rerun") != null;
    
    private Object testInstance;
    private Object[] arguments;
    
    private InjectionValues(Object testInstance, Object... arguments) {
        this.testInstance = testInstance;
        this.arguments = arguments;
    }
    
    public static InjectionValues from(Example example) throws Exception {
        if (FORCE_RERUN) return __forceRerun(example);
        int length = example.method.arity();
        Object[] values = new Object[length];
        for (int i = 0; i < length; i++) {
            values[i] = example.node.dependencies()
                .get(i)
                .dependency()
                .returnValue
                .get(example.policy);
        }
        Object testInstance = null;
        if (example.policy.cloneTestCase() && (!example.node.dependencies().isEmpty() && example.node.dependencies().get(0).dependency().returnValue.hasTestCaseInstance(example.method.getActualClass()))) {
            testInstance = example.node.dependencies().get(0).dependency().returnValue.getTestCaseInstance();
        }
        else {
            testInstance = CloneUtil.getConstructor(example.method.getActualClass()).newInstance();
        }
        return new InjectionValues(testInstance, values);
    }
    
    private static InjectionValues __forceRerun(Example example) throws Exception {
        int length = example.method.arity();
        Object[] values = new Object[length];
        for (int i = 0; i < length; i++) {
            Example each = example.node.dependencies().get(i).dependency();
            each.bareInvoke();
            values[i] = each.returnValue.getValue();
        }
        Object testInstance = null;
        if (!example.node.dependencies().isEmpty() 
                && example.node.dependencies().get(0).dependency().returnValue.hasTestCaseInstance(example.method.getActualClass())) {
            testInstance = example.node.dependencies().get(0).dependency().returnValue.getTestCaseInstance();
        }
        else {
            testInstance = CloneUtil.getConstructor(example.method.getActualClass()).newInstance();
        }
        return new InjectionValues(testInstance, values);
    }

    public Object getTestInstance() {
        return testInstance;
    }
    
    public Object[] getArguments() {
        return arguments;
    }
    
}
