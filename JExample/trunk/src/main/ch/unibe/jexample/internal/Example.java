package ch.unibe.jexample.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import ch.unibe.jexample.internal.graph.Edge;
import ch.unibe.jexample.internal.graph.Node;
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

    public final Class<? extends Throwable> expectedException;
    public final MethodReference method;
    public final Node<Example> node;
    public final ExampleClass owner;

    JExampleError errors;

    private final Description description;
    private ReturnValue returnValue;

    /*default*/ Example(MethodReference method, ExampleClass owner) {
        assert method != null && owner != null;
        this.owner = owner;
        this.method = method;
        this.description = method.createTestDescription();
        this.returnValue = ReturnValue.R_NONE;
        this.expectedException = method.initExpectedException();
        this.node = new Node<Example>(this);
    }

    public Description getDescription() {
        return description;
    }

    public ReturnValue getReturnValue() {
        return returnValue;
    }

    public boolean hasErrors() {
        if (errors == null) this.validate();
        return errors.size() > 0;
    }

    public void run(RunNotifier notifier) {
        if (returnValue.isNull()) returnValue = new ExampleRunner(this, notifier).run();
        this.maybeRemoveMyselfFromMyProducersConsumersList();
    }

    @Override
    public String toString() {
        return "Example: " + method;
    }

    public boolean wasSuccessful() {
        return getReturnValue().isGreen();
    }

    void initializeDependencies(ExampleGraph exampleGraph) {
        for (MethodReference m: collectDependencies()) {
            if (m.isBroken()) {
                node.makeBrokenEdge(m.getError());
            }
            else {
                Example d = exampleGraph.makeExample(m);
                node.addProvider(d.node);
            }
        }
    }

    protected ReturnValue bareInvoke() throws Exception {
        owner.runBeforeClassBefores();
        InjectionValues injection = new InjectionValues(this);
        Object newResult = method.invoke(injection.getTestInstance(), injection.getArguments());
        return new ReturnValue(newResult, injection.getTestInstance());
    }

    protected Iterable<MethodReference> collectDependencies() {
        String declaration = method.getDependencyString();
        try {
            Collection<MethodReference> all = new ArrayList<MethodReference>();
            Iterable<MethodLocator> locators = MethodLocator.parseAll(declaration);
            for (MethodLocator each: locators) all.add(each.resolve(method.getActualClass()));
            return all;
        } catch (InvalidDeclarationError ex) {
            return Collections.singleton(new MethodReference(ex));
        }
    }

    private void maybeRemoveMyselfFromMyProducersConsumersList() {
        if (!DISPOSE_IF_DONE || !node.consumers().isEmpty()) return;
        throw new RuntimeException("TODO");
//        this.returnValue.dispose();
//        for (Edge each: this.node.dependencies()) {
//            if (each.isBroken()) continue;
//            each.getProducer().node.consumers().remove(this);
//            each.getProducer().maybeRemoveMyselfFromMyProducersConsumersList();
//        }
    }

    private void validate() {
        errors = new JExampleError();
        if (this.node.isPartOfCycle()) {
            errors.add(Kind.RECURSIVE_DEPENDENCIES, "Part of a cycle!");
        }
        if (!method.isTestAnnotationPresent()) errors.add(
                Kind.MISSING_ANNOTATION, 
                "Method %s is not a test method, missing @Test or @Given annotation.",
                this);
        int d = this.node.dependencies().size();
        int p = method.arity();
        if (p > d) {
            errors.add(Kind.MISSING_PROVIDERS, "Method %s has %d parameters but only %d dependencies.", toString(), p,
                    d);
        } else {
            validateDependencyTypes();
        }
    }

    private void validateDependencyTypes() {
        Iterator<Edge<Example>> tms = this.node.dependencies().iterator();
        int position = 1;
        for (Class<?> t: method.getParameterTypes()) {
            Edge<Example> each = tms.next();
            if (each.isBroken()) {
                errors.add(Kind.NO_SUCH_PROVIDER,
                        each.getError());
                continue;
            }
            Example tm = each.getProducer().value;
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
            Edge<Example> each = tms.next();
            if (each.isBroken()) {
                errors.add(Kind.NO_SUCH_PROVIDER,
                        each.getError());
                continue;
            }
        }
    }

}
