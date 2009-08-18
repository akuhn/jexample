package ch.unibe.jexample.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class ExampleNode {

    // TODO 1) move all graph navigation to this class
    // TODO 2) move all graph validation to this class
    // TODO 3) rewrite isolated tests for ExampleNode & Dependency
    
    private List<Dependency> producers;
    private Collection<Example> consumers; // TODO Collection<Dependency>
    public final Example value;
    
    public ExampleNode(Example example) {
        this.producers = new ArrayList<Dependency>();
        this.consumers = new HashSet<Example>();
        this.value = example;
    }
    
    public List<Dependency> dependencies() {
        return Collections.unmodifiableList(producers);
    }
    
    public Collection<Example> consumers() {
        return Collections.unmodifiableCollection(consumers);
    }

    public void __consumersAdd(Example value2) {
        consumers.add(value2);
    }

    public void __producersAdd(Dependency dependency) {
        producers.add(dependency);
    }

    public Collection<ExampleNode> transitiveClosure() {
        return collectTransitiveClosureInto(new HashSet<ExampleNode>());
    }
    
    private Collection<ExampleNode> collectTransitiveClosureInto(Collection<ExampleNode> all) {
        for (ExampleNode node: producers()) if (all.add(node)) node.collectTransitiveClosureInto(all);
        return all;
    }

    public Iterable<ExampleNode> producers() {
        ArrayList<ExampleNode> nodes = new ArrayList<ExampleNode>();
        for (Dependency each: producers) if (!each.isBroken()) nodes.add(each.getProducer());
        return Collections.unmodifiableCollection(nodes);
    }

    public void validateCycle() {
        validateCycle(this, new Stack<ExampleNode>(), new HashSet<ExampleNode>());
    }
    
    private void validateCycle(ExampleNode start, Stack<ExampleNode> cycle, Set<ExampleNode> done) {
        if (done.add(this)) {
            for (ExampleNode node: producers()) {
                cycle.push(node);
                if (start == node) invalidate(cycle);
                node.validateCycle(start, cycle, done);
                cycle.pop();
            }
        }
    }

    private void invalidate(Stack<ExampleNode> cycle) {
        for (ExampleNode each: cycle) each.value.errorPartOfCycle();
    }
    
}
