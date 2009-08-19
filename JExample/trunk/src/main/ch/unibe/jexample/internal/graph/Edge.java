package ch.unibe.jexample.internal.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Stack;


public class Edge<E> {

    /*default*/ final Node<E> consumer;
    /*default*/ final Node<E> producer;
    private final Throwable broken;
    private Collection<Cycle<E>> cycles;
    
    /*default*/ Edge(Node<E> consumer, Node<E> producer) {
        this.consumer = consumer;
        this.producer = producer;
        this.broken = null;
        producer.consumers.add(this);
        consumer.producers.add(this);
        this.detectCyclicDependencies();
    }

    /*default*/ Edge(Node<E> consumer, Throwable broken) {
        this.consumer = consumer;
        this.broken = broken;
        this.producer = null;
        consumer.producers.add(this);
    }

    public boolean isBroken() {
        return broken != null;
    }

    public Node<E> getProducer() {
        if (isBroken()) throw new IllegalStateException();
        return producer;
    }

    public Throwable getError() {
        return broken;
    }

    private void detectCyclicDependencies() {
        this.validateCycle(this, new Stack<Edge<E>>(), new HashSet<Edge<E>>());
    }
    
    private void validateCycle(Edge<E> initial, Stack<Edge<E>> stack, HashSet<Edge<E>> hashSet) {
        stack.push(this);
        if (hashSet.add(this)) {
            for (Edge<E> each: this.producer.dependencies()) {
                if (initial == each) invalidate(stack);
                each.validateCycle(initial, stack, hashSet);
            }
        }
        stack.pop();
    }

    private void invalidate(Stack<Edge<E>> stack) {
        Cycle<E> cycle = new Cycle<E>(stack);
        for (Edge<E> each: stack) each.addCycle(cycle);
    }

    private void addCycle(Cycle<E> cycle) {
        if (cycles == null) cycles = new ArrayList<Cycle<E>>();
        cycles.add(cycle);
    }

    public boolean isPartOfCycle() {
        return !(cycles == null || cycles.isEmpty());
    }
    
    public Collection<Cycle<E>> cycles() {
        if (cycles == null) return Collections.emptyList();
        return Collections.unmodifiableCollection(cycles);
    }
    
}
