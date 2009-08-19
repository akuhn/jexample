package ch.unibe.jexample.internal.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/** Node in a graph.
 * 
 * @author Adrian Kuhn, 2009
 *
 */
public class Node<E> {

    // TODO 3) rewrite isolated tests for ExampleNode & Dependency
    
    /*default*/ List<Edge<E>> producers;
    /*default*/ Collection<Edge<E>> consumers;
    public final E value;
    
    public Node(E example) {
        this.producers = new ArrayList<Edge<E>>();
        this.consumers = new ArrayList<Edge<E>>();
        this.value = example;
    }
    
    public List<Edge<E>> dependencies() {
        return Collections.unmodifiableList(producers);
    }
    
    public Collection<E> consumers() {
        ArrayList<E> nodes = new ArrayList<E>();
        for (Edge<E> each: consumers) nodes.add(each.getProducerNode().value);
        return Collections.unmodifiableCollection(nodes);
    }

    public Collection<Node<E>> transitiveClosure() {
        return collectTransitiveClosureInto(new HashSet<Node<E>>());
    }
    
    private Collection<Node<E>> collectTransitiveClosureInto(Collection<Node<E>> all) {
        for (Node<E> node: producers()) if (all.add(node)) node.collectTransitiveClosureInto(all);
        return all;
    }

    private Iterable<Node<E>> producers() {
        ArrayList<Node<E>> nodes = new ArrayList<Node<E>>();
        for (Edge<E> each: producers) if (!each.isBroken()) nodes.add(each.getProducerNode());
        return Collections.unmodifiableCollection(nodes);
    }

    public E firstProducerOrNull() {
        if (producers.isEmpty()) return null;
        Edge<E> d = producers.get(0);
        return d.getProducerNode().value;
    }

    public boolean isPartOfCycle() {
        for (Edge<E> each: producers) if (each.isPartOfCycle()) return true;
        return false;
    }
      
    public void makeEdge(Node<E> dependency) {
        new Edge<E>(dependency, this);
    }

    public void makeBrokenEdge(Throwable ex) {
        new Edge<E>(ex, this);
    }
    
}
