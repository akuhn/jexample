package ch.unibe.jexample.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;


public class Dependency {

    private final Node consumer;
    private final Node producer;
    private final Throwable broken;
    private Collection<Collection<Dependency>> cycles;
    
    public Dependency(Node producer, Node consumer) {
        this.consumer = consumer;
        this.producer = producer;
        this.broken = null;
        producer.__consumersAdd(this);
        consumer.__producersAdd(this);
    }

    public Dependency(Throwable broken, Node consumer) {
        this.consumer = consumer;
        this.broken = broken;
        this.producer = null;
        consumer.__producersAdd(this);
    }

    public boolean isBroken() {
        return broken != null;
    }

    public Example getProducer() {
        return getProducerNode().value;
    }
    
    public Node getProducerNode() {
        if (isBroken()) throw new IllegalStateException();
        return producer;
    }

    public Throwable getError() {
        return broken;
    }

    @Override
    public String toString() {
        return broken == null ? producer.toString() : broken.toString();
    }

    public Node getConsumerNode() {
        return consumer;
    }
    
    public void validateCycle() {
        this.validateCycle(this, new Stack<Dependency>(), new HashSet<Dependency>());
    }
    
    private void validateCycle(Dependency initial, Stack<Dependency> stack, HashSet<Dependency> hashSet) {
        if (hashSet.add(this)) {
            for (Dependency each: this.producer.dependencies()) {
                stack.push(each);
                if (initial == each) invalidate(stack);
                each.validateCycle(initial, stack, hashSet);
                stack.pop();
            }
        }
    }

    private void invalidate(Stack<Dependency> stack) {
        for (Dependency each: stack) each.addCycle(new ArrayList<Dependency>(stack));
    }

    private void addCycle(ArrayList<Dependency> cycle) {
        if (cycles == null) cycles = new ArrayList<Collection<Dependency>>();
        cycles.add(cycle);
    }

    public boolean isPartOfCycle() {
        return !(cycles == null || cycles.isEmpty());
    }
    
    
}
