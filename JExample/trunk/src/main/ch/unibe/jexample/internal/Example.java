package ch.unibe.jexample.internal;

import static ch.unibe.jexample.InjectionPolicy.CLONE;
import static ch.unibe.jexample.InjectionPolicy.DEEPCOPY;
import static ch.unibe.jexample.InjectionPolicy.NONE;
import static ch.unibe.jexample.InjectionPolicy.RERUN;

import java.util.Iterator;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import ch.unibe.jexample.InjectionPolicy;
import ch.unibe.jexample.internal.graph.Consumers;
import ch.unibe.jexample.internal.graph.Edge;
import ch.unibe.jexample.internal.graph.Node;
import ch.unibe.jexample.internal.graph.Producers;
import ch.unibe.jexample.internal.util.JExampleError;
import ch.unibe.jexample.internal.util.MethodReference;
import ch.unibe.jexample.internal.util.Reflection;
import ch.unibe.jexample.internal.util.JExampleError.Kind;

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
 * {@link ch.unibe.jexample.InjectionPolicy @InjectionPolicy}.
 * <p>
 * An example method must have an {@link org.junit.Test @Test}
 * annotation or an {@link ch.unibe.jexample.Given @Given}.
 * The enclosing class must use an {@link org.junit.RunWith &#64;RunWith}
 * annotation to declare {@link ch.unibe.jexample.JExample
 * JExample} as test runner.
 * <p>
 * An example method may return an instance of its unit under test.
 * <p>
 * An example method may depend on both successful execution and return value of
 * other examples. If it does, it must declare the dependencies using an
 * {@link ch.unibe.jexample.Given @Given} annotation. An example method with
 * dependencies may have method parameters. The number of parameters must be
 * less than or equal to the number of dependencies. The type of the n-th
 * parameter must match the return type of the n-th dependency.
 * 
 * @author Lea Haensenberger
 * @author Adrian Kuhn
 */
public class Example {

    public final MethodReference method;

    private final Description description;
    private JExampleError errors;
    private final Node<Example> node;
    private final ExampleClass owner;
    private ReturnValue returnValue;
    private boolean sticky = false;

    /*default*/ Example(MethodReference method, ExampleClass owner) {
        assert method != null && owner != null;
        this.owner = owner;
        this.method = method;
        this.description = method.createTestDescription();
        this.returnValue = ReturnValue.PENDING;
        this.node = new Node<Example>(this);
    }

    /** Do not flush cache of this example when done.
     * 
     */
    public void beSticky() {
        this.sticky = true;
    }

    /** Outgoing dependencies.
     * 
     */
    public Consumers<Example> consumers() {
        return node.consumers();
    }

    /** Get cached return values of all producers (up to the number of required arguments).
     * 
     */
    public Object[] fetchArguments() {
        Object[] arguments = new Object[method.arity()];
        for (int n = 0; n < arguments.length; n++) arguments[n] = producers().get(n).returnValue().getValue();
        return arguments;
    }

    /** Returns cached receiver of first producer (or creates a new receiver instance).
     * The receiver of an example must be an instance of the defining class. 
     * If the producer is not defined in the same class as this example, a new receiver is created.
     * 
     */
    public Object fetchReceiver() {
        Object receiver = null;
        if (!producers().isEmpty()) receiver = producers().first().returnValue().getTestCaseInstance();
        if (!method.getActualClass().isInstance(receiver)) receiver = null;
        if (receiver == null) receiver = Reflection.newInstance(method.getActualClass());
        assert receiver != null;
        return receiver;        
    }

    /** Used to fetch return values when cloning has been failed
     * (see {@link InjectionValues}).
     * Re-runs this example if no cached return value is available,
     * returns the return value (whether newly acquired or cached),
     * and eventually flushes the cache.  
     * 
     */
    public ReturnValue fetchReturnValueAndFlush() throws Exception {
        ReturnValue value = returnValue;
        returnValue = returnValue.withoutCache();
        return value.isMissing() ? this.bareInvoke() : value;
    }

    public Description getDescription() {
        return description;
    }

    public JExampleError getErrors() {
        if (errors == null) {
            errors = new JExampleError();
            this.shouldHaveAnnotation();
            this.dependenciesShouldNotBeCyclic();
            this.dependenciesShouldExist();
            this.shouldHaveEnoughProducers();
            this.producersShouldBeAssignable();
            this.producersShouldNotExpectException();
        }
        return errors;
    }

    public ReturnValue returnValue() {
        return returnValue;
    }

    public boolean hasErrors() {
        return getErrors().size() > 0;
    }

    /** Returns true of this example and all its descendants are done.
     *<P> 
     * TODO take into account whether a consumer actually requires a return value or not!
     * 
     */
    public boolean isDone() {
        if (hasErrors() || returnValue.isRed() || returnValue.isWhite()) return true;
        for (Example each: consumers()) if (!each.isDone()) return false;
        return returnValue.hasBeenRun();
    }

    /** Incoming dependencies
     * (the first <I>n</I> are producing the parameters of this example).
     * 
     */
    public Producers<Example> producers() {
        return node.producers();
    }

    public InjectionStrategy resolveInjectionStrategy() {
        InjectionPolicy policy = method.getInjectionPolicy();
        InjectionPolicy resolution = policy.resolve();
        if (resolution == CLONE) return new CloneInjectionStrategy();
        if (resolution == DEEPCOPY) return new DeepcopyInjectionStrategy();
        if (resolution == NONE) return new NoneInjectionStrategy();
        if (resolution == RERUN) return new RerunInjectionStrategy();
        throw new AssertionError();
    }

    /** Runs this example and caches the return value. 
     * Return value is only cached if there are pending consumers.
     * Before running the example all producers are run: 
     * if any of the producers fails, this example is skipped.
     * 
     *<P>
     * If this example has been run before it is not re-run, 
     * use {@link #bareInvoke} to force a re-run.
     * 
     */
    public void run(RunNotifier notifier) {
        if (!returnValue.hasBeenRun()) returnValue = new ExampleRunner(this, notifier).run();
        if (this.isDone() && !sticky) returnValue = returnValue.withoutCache();
    }

    @Override
    public String toString() {
        return "Example: " + method;
    }

    public boolean wasSuccessful() {
        return returnValue().isGreen();
    }
    
    /*default*/ void initializeDependencies(ExampleGraph exampleGraph) {
        for (MethodReference m: method.collectDependencies()) {
            if (m.isBroken()) {
                node.makeBrokenEdge(m.getError());
            }
            else {
                Example d = exampleGraph.makeExample(m);
                node.addProvider(d.node);
            }
        }
    }
 
    /** Runs the example and returns its return value.
     * Assumes all producers had been successful.
     */
    protected ReturnValue bareInvoke() throws Exception {
        for (Example producer: producers()) assert producer.wasSuccessful();
        owner.runBeforeClassBefores();
        InjectionValues injection = InjectionValues.make(this);
        Object newResult = method.invoke(injection.getReceiver(), injection.getArguments());
        return new ReturnValue(newResult, injection.getReceiver());
    }
    
    private void dependenciesShouldExist() {
        for (Edge<Example> each: node.producers().edges()) {
            if (each.isBroken()) errors.add(Kind.NO_SUCH_PROVIDER, each.getError());
        }
    }

    private void dependenciesShouldNotBeCyclic() {
        if (this.node.isPartOfCycle()) errors.add(Kind.RECURSIVE_DEPENDENCIES, "Part of a cycle!");
    }

    private void producersShouldBeAssignable() {
        Class<?>[] types = this.method.getParameterTypes();
        int n = 0;
        for (Edge<Example> edge: producers().edges()) {
            if (++n > method.arity()) break; // skip non-injected producers
            if (edge.isBroken()) continue;
            Example producer = edge.getProducer().value;
            Class<?> returnType = producer.method.getReturnType();
            if (!types[n-1].isAssignableFrom(returnType))
                errors.add(Kind.PARAMETER_NOT_ASSIGNABLE,
                        "Parameter #%d in (%s) is not assignable from depedency (%s).", 
                        n, method, producer.method);
        }
    }

    private void producersShouldNotExpectException() {
        int n = 0;
        for (Edge<Example> edge: producers().edges()) {
            if (++n > method.arity()) break; // skip non-injected producers
            if (edge.isBroken()) continue;
            Example each = edge.getProducer().value;
            if (each.method.expectedException() != null) 
                 errors.add(Kind.PROVIDER_EXPECTS_EXCEPTION,
                            "(%s): invalid dependency (%s), provider must not expect exception.", method, each.method);
        }
    }

    private void shouldHaveAnnotation() {
        if (!method.isTestAnnotationPresent()) 
            errors.add(Kind.MISSING_ANNOTATION, 
                    "Method %s is not a test method, missing @Test or @Given annotation.",
                    this);
    }

    private void shouldHaveEnoughProducers() {
        int d = this.node.producers().size();
        int p = method.arity();
        if (d < p) errors.add(Kind.MISSING_PROVIDERS, 
                "Method %s has %d parameters but only %d dependencies.",
                toString(), p, d);
    }
   
}
