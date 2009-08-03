package ch.unibe.jexample.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Dependency {

    private ExampleNode consumer;
    private ExampleNode producer;
    private Throwable broken;

    public Dependency(ExampleNode dependency, ExampleNode consumer) {
        this.producer = dependency;
        this.consumer = consumer;
        producer.consumers.add(consumer.value);
    }

    public Dependency(Throwable broken) {
        this.broken = broken;
    }

    public boolean isBroken() {
        return broken != null;
    }

    public Example dependency() {
        if (isBroken()) throw new RuntimeException(broken);
        return producer.value;
    }

    public Throwable getError() {
        return broken;
    }

    @Override
    public String toString() {
        return broken == null ? producer.toString() : broken.toString();
    }

    public void validateCycle() {
        validateCycle(consumer, new Stack<ExampleNode>(), new HashSet<ExampleNode>());
    }
    
    private void validateCycle(ExampleNode example, Stack<ExampleNode> cycle, Set<ExampleNode> done) {
        if (this.isBroken()) return;
        cycle.push(producer);
        if (example == producer) invalidate(cycle);
        if (done.add(producer)) {
            for (Dependency each: producer.producers) each.validateCycle(example, cycle, done);
        }
        cycle.pop();
    }

    private void invalidate(Stack<ExampleNode> cycle) {
        for (ExampleNode each: cycle) each.value.errorPartOfCycle();
    }
    
}
