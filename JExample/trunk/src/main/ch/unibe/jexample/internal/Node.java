package ch.unibe.jexample.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Node {

    // TODO 3) rewrite isolated tests for ExampleNode & Dependency
    
    private List<Dependency> producers;
    private Collection<Dependency> consumers;
    public final Example value;
    
    public Node(Example example) {
        this.producers = new ArrayList<Dependency>();
        this.consumers = new ArrayList<Dependency>();
        this.value = example;
    }
    
    public List<Dependency> dependencies() {
        return Collections.unmodifiableList(producers);
    }
    
    public Collection<Example> consumers() {
        ArrayList<Example> nodes = new ArrayList<Example>();
        for (Dependency each: consumers) nodes.add(each.getProducer());
        return Collections.unmodifiableCollection(nodes);
    }

    public void __consumersAdd(Dependency d) {
        assert d.getProducerNode() == this;
        consumers.add(d);
    }

    public void __producersAdd(Dependency d) {
        assert d.getConsumerNode() == this;
        producers.add(d);
    }

    public Collection<Node> transitiveClosure() {
        return collectTransitiveClosureInto(new HashSet<Node>());
    }
    
    private Collection<Node> collectTransitiveClosureInto(Collection<Node> all) {
        for (Node node: producers()) if (all.add(node)) node.collectTransitiveClosureInto(all);
        return all;
    }

    private Iterable<Node> producers() {
        ArrayList<Node> nodes = new ArrayList<Node>();
        for (Dependency each: producers) if (!each.isBroken()) nodes.add(each.getProducerNode());
        return Collections.unmodifiableCollection(nodes);
    }

    public Example firstProducerOrNull() {
        if (producers.isEmpty()) return null;
        Dependency d = producers.get(0);
        return d.getProducer();
    }

    public boolean isPartOfCycle() {
        for (Dependency each: producers) if (each.isPartOfCycle()) return true;
        return false;
    }
        
}
