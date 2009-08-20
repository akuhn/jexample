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
    
    public final E value;
    /*default*/ Collection<Edge<E>> consumers;
    /*default*/ List<Edge<E>> producers;
    
    public Node(E example) {
        this.producers = new ArrayList<Edge<E>>();
        this.consumers = new ArrayList<Edge<E>>();
        this.value = example;
    }
    
    public void addProvider(Node<E> dependency) {
        new Edge<E>(this, dependency);
    }
    
    public Collection<E> consumers() {
        ArrayList<E> nodes = new ArrayList<E>();
        for (Edge<E> each: consumers) nodes.add(each.consumer.value);
        return Collections.unmodifiableCollection(nodes);
    }

    public List<Edge<E>> dependencies() {
        return Collections.unmodifiableList(producers);
    }
    
    public E firstProducerOrNull() {
        if (producers.isEmpty()) return null;
        Edge<E> d = producers.get(0);
        return d.getProducer().value;
    }

    public boolean isPartOfCycle() {
        for (Edge<E> each: producers) if (each.isPartOfCycle()) return true;
        return false;
    }

    public void makeBrokenEdge(Throwable ex) {
        new Edge<E>(this, ex);
    }

    public Collection<E> producers() {
        ArrayList<E> nodes = new ArrayList<E>();
        for (Edge<E> each: producers) nodes.add(each.producer.value);
        return Collections.unmodifiableCollection(nodes);
    }
      
    public Collection<Node<E>> transitiveClosure() {
        return collectTransitiveClosureInto(new HashSet<Node<E>>());
    }

    private Collection<Node<E>> collectTransitiveClosureInto(Collection<Node<E>> all) {
        for (Edge<E> each: producers) {
            Node<E> producer = each.getProducer();
            if (all.add(producer)) producer.collectTransitiveClosureInto(all);
        }
        return all;
    }
    
}
