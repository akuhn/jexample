package ch.unibe.jexample.internal.tests;

import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;

import ch.unibe.jexample.internal.Example;
import ch.unibe.jexample.internal.ExampleGraph;
import ch.unibe.jexample.internal.util.JExampleError;

public abstract class Util {

    public static Example runExample(Class<?> container, String name) {
        ExampleGraph g = new ExampleGraph();
        try {
            g.add(container);
        } catch (JExampleError ex) {
            throw new RuntimeException(ex);
        }
        Example e = g.findExample(container, name);
        e.run(new RunNotifier());
        return e;
    }

    public static Result runAllExamples(Class<?>... containers) {
        ExampleGraph g = new ExampleGraph();
        try {
            return g.runJExample(containers);
        } catch (JExampleError ex) {
            throw new RuntimeException(ex);
        }
    }

}
