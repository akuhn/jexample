package ch.unibe.jexample.internal.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Stack;


public class Dependency<E> {

    /*default*/ final Node<E> consumer;
    /*default*/ final Node<E> producer;
    private final Throwable broken;
    private boolean isInjection = false;
    private Collection<Cycle<E>> cycles;
    
    /*default*/ Dependency(Node<E> consumer, Node<E> producer) {
        this.consumer = consumer;
        this.producer = producer;
        this.broken = null;
        producer.consumers.add(this);
        consumer.producers.add(this);
        this.detectCyclicDependencies();
    }

    /*default*/ Dependency(Node<E> consumer, Throwable broken) {
        this.consumer = consumer;
        this.broken = broken;
        this.producer = null;
        consumer.producers.add(this);
    }

    public Collection<Cycle<E>> cycles() {
        if (cycles == null) return Collections.emptyList();
        return Collections.unmodifiableCollection(cycles);
    }

    public Throwable getError() {
        return broken;
    }

    public Node<E> getProducer() {
        if (isBroken()) throw new IllegalStateException();
        return producer;
    }

    public boolean isBroken() {
        return broken != null;
    }
    
    public boolean isPartOfCycle() {
        return !(cycles == null || cycles.isEmpty());
    }

    private void addCycle(Cycle<E> cycle) {
        if (cycles == null) cycles = new ArrayList<Cycle<E>>();
        cycles.add(cycle);
    }

    private void detectCyclicDependencies() {
        this.validateCycle(this, new Stack<Dependency<E>>(), new HashSet<Dependency<E>>());
    }

    private void invalidate(Stack<Dependency<E>> stack) {
        Cycle<E> cycle = new Cycle<E>(stack);
        for (Dependency<E> each: stack) each.addCycle(cycle);
    }
    
    private void validateCycle(Dependency<E> initial, Stack<Dependency<E>> stack, HashSet<Dependency<E>> hashSet) {
        stack.push(this);
        if (hashSet.add(this)) {
            for (Dependency<E> each: this.producer.producers().edges()) {
                if (initial == each) invalidate(stack);
                each.validateCycle(initial, stack, hashSet);
            }
        }
        stack.pop();
    }

    public void setInjection(boolean isInjection) {
        this.isInjection = isInjection;
    }

    public boolean isInjection() {
        return isInjection;
    }
    
}
