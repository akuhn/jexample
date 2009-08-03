package ch.unibe.jexample.internal;

import static ch.unibe.jexample.internal.ExampleColor.NONE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExampleOptions;
import ch.unibe.jexample.util.CloneUtil;
import ch.unibe.jexample.util.InvalidDeclarationError;
import ch.unibe.jexample.util.JExampleError;
import ch.unibe.jexample.util.MethodLocator;
import ch.unibe.jexample.util.MethodReference;
import ch.unibe.jexample.util.JExampleError.Kind;

/**
 * A test method with dependencies and return value. Test methods are written
 * source code to illustrate the usage of the unit under test, and may return a
 * characteristic instance of its unit under test. Thus, test methods are in
 * fact <i>examples</i> of the unit under test.
 * <p>
 * When executing an example method, the JExample framework caches its return
 * value. If an example method declares dependencies and has arguments, the
 * framework will inject the cache return values of the dependencies as
 * parameters into the method execution. For more details, please refer to
 * {@link ch.unibe.jexample.JExampleOptions @InjectionPolicy}.
 * <p>
 * An example method must have at least a {@link org.junit.Test @Test}
 * annotation. The enclosing class must use an {@link org.junit.RunWith
 * &#64;RunWith} annotation to declare {@link ch.unibe.jexample.JExample
 * JExampleRunner} as test runner.
 * <p>
 * An example method may return an instance of its unit under test.
 * <p>
 * An example method may depend on both successful execution and return value of
 * other examples. If it does, it must declare the dependencies using an
 * {@link ch.unibe.jexample.Given @Depends} annotation. An example methods with
 * dependencies may have method parameters. The number of parameters must be
 * less than or equal to the number of dependencies. The type of the n-th
 * parameter must match the return type of the n-th dependency.
 * 
 * @author Lea Haensenberger
 * @author Adrian Kuhn
 */
public class Example {

    public static boolean DISPOSE_IF_DONE = false;

    public final ReturnValue returnValue;
    public final MethodReference method;
    public final ExampleClass owner;
    public final Class<? extends Throwable> expectedException;

    final ExampleNode node;

    private final Description description;
    protected JExampleError errors;
    protected JExampleOptions policy;

    /*default*/ Example(MethodReference method, ExampleClass owner) {
        assert method != null && owner != null;
        this.owner = owner;
        this.method = method;
        this.description = method.createTestDescription();
        this.returnValue = new ReturnValue(this);
        this.policy = initJExampleOptions(method.getActualClass());
        this.errors = new JExampleError();
        this.expectedException = initExpectedException();
        this.node = new ExampleNode(this);
    }

    protected Iterable<MethodReference> collectDependencies() {
        Collection<MethodReference> all = new ArrayList<MethodReference>();
        Given a = method.getAnnotation(Given.class);
        if (a == null) return all;
        try {
            Iterable<MethodLocator> locators = MethodLocator.parseAll(a.value());
            for (MethodLocator each: locators) {
                all.add(each.resolve(method.getActualClass()));
            }
        } catch (InvalidDeclarationError ex) {
            errors.add(Kind.INVALID_DEPENDS_DECLARATION, ex);
        }
        return all;
    }

    protected void errorPartOfCycle() {
        errors.add(Kind.RECURSIVE_DEPENDENCIES, "Part of a cycle!");
    }

    private Class<? extends Throwable> initExpectedException() {
        Test a = this.method.getAnnotation(Test.class);
        if (a == null) return null;
        if (a.expected() == org.junit.Test.None.class) return null;
        return a.expected();
    }

    private JExampleOptions initJExampleOptions(Class<?> jclass) {
        final JExampleOptions options = (JExampleOptions) jclass.getAnnotation(JExampleOptions.class);
        if (options == null) return JExampleOptions.class.getAnnotation(JExampleOptions.class);
        return options;
    }

    private Object getContainerInstance() throws Exception {
        if (this.policy.cloneTestCase() && (!this.producers().isEmpty() && this.producers().get(0).dependency().returnValue.hasTestCaseInstance(this.method.getActualClass()))) {
            return this.producers().get(0).dependency().returnValue.getTestCaseInstance();
        }
        return CloneUtil.getConstructor(method.getActualClass()).newInstance();
    }
    
    public Collection<Example> transitiveClosure() {
        Collection<Example> all = new HashSet<Example>();
        this.collectTransitiveClosureInto(all);
        return all;
    }

    private void collectTransitiveClosureInto(Collection<Example> all) {
        for (Dependency each: this.producers()) {
            if (each.isBroken()) continue;
            Example eg = each.dependency();
            if (all.add(eg)) eg.collectTransitiveClosureInto(all);
        }
    }
    

    protected Object bareInvoke() throws Exception {
        owner.runBeforeClassBefores();
        Object[] values = getInjectionValues();
        Object[] args = values;
        Object container = getContainerInstance();
        Object newResult = method.invoke(container, args);
        if (returnValue.color == NONE) { 
            // XXX why do we store the first result, and not the most recent one? 
            returnValue.assign(newResult);
            returnValue.assignInstance(container);
        }
        return newResult;
    }

    private Object[] getInjectionValues() throws Exception {
        int length = method.arity();
        Object[] values = new Object[length];
        for (int i = 0; i < length; i++) {
            values[i] = this.producers().get(i).dependency().returnValue.get(policy);
        }
        return values;
    }

    public void run(RunNotifier notifier) {
        if (returnValue.color == NONE) returnValue.color = new ExampleRunner(this, notifier).run();
        this.maybeRemoveMyselfFromMyProducersConsumersList();
    }

    private void maybeRemoveMyselfFromMyProducersConsumersList() {
        if (!DISPOSE_IF_DONE || !consumers().isEmpty()) return;
        this.returnValue.dispose();
        for (Dependency each: this.producers()) {
            if (each.isBroken()) continue;
            each.dependency().consumers().remove(this);
            each.dependency().maybeRemoveMyselfFromMyProducersConsumersList();
        }
    }

    @Override
    public String toString() {
        return "Example: " + method;
    }

    protected void validate() {
        if (!method.isAnnotationPresent(Test.class)) {
            errors.add(Kind.MISSING_TEST_ANNOTATION, "Method %s is not a test method, missing @Test annotation.",
                    toString());
        }
        int d = this.producers().size();
        int p = method.arity();
        if (p > d) {
            errors.add(Kind.MISSING_PROVIDERS, "Method %s has %d parameters but only %d dependencies.", toString(), p,
                    d);
        } else {
            validateDependencyTypes();
        }
        // if (providers.transitiveClosure().contains(this)) {
        // errors.add(Kind.RECURSIVE_DEPENDENCIES,
        // "Recursive dependency found.");
        // }
    }

    private void validateDependencyTypes() {
        Iterator<Dependency> tms = this.producers().iterator();
        int position = 1;
        for (Class<?> t: method.getParameterTypes()) {
            Dependency each = tms.next();
            if (each.isBroken()) {
                errors.add(Kind.NO_SUCH_PROVIDER,
                        each.getError());
                continue;
            }
            Example tm = each.dependency();
            Class<?> r = tm.method.getReturnType();
            if (!t.isAssignableFrom(r)) {
                errors.add(Kind.PARAMETER_NOT_ASSIGNABLE,
                        "Parameter #%d in (%s) is not assignable from depedency (%s).", position, method, tm.method);
            }
            if (tm.expectedException != null) {
                errors.add(Kind.PROVIDER_EXPECTS_EXCEPTION,
                        "(%s): invalid dependency (%s), provider must not expect exception.", method, tm.method);
            }
            position++;
        }
        while (tms.hasNext()) {
            Dependency each = tms.next();
            if (each.isBroken()) {
                errors.add(Kind.NO_SUCH_PROVIDER,
                        each.getError());
                continue;
            }
        }
    }

    public boolean wasSuccessful() {
        return returnValue.color == ExampleColor.GREEN;
    }

    public void addConsumer(Example consumer) {
        consumers().add(consumer);
    }

    public Description getDescription() {
        return description;
    }

    public List<Dependency> producers() {
        return node.producers;
    }

    private Collection<Example> consumers() {
        return node.consumers;
    }

}
