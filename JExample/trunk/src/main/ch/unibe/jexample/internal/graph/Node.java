package ch.unibe.jexample.internal.graph;

import java.util.Collection;
import java.util.HashSet;

/** Node in a graph.
 * 
 * @author Adrian Kuhn, 2009
 *
 */
public class Node<E> {

    public final E value;
    protected Consumers<E> consumers;
    protected Producers<E> producers;
    
    public Node(E example) {
        this.producers = new Producers<E>();
        this.consumers = new Consumers<E>();
        this.value = example;
    }
    
    public Consumers<E> consumers() {
        return consumers;
    }

    public boolean isPartOfCycle() {
        for (Edge<E> each: producers.edges()) if (each.isPartOfCycle()) return true;
        return false;
    }

    public Producers<E> producers() {
        return producers;
    }
      
    public Collection<E> transitiveClosure() {
        return collectTransitiveClosureInto(new HashSet<E>());
    }

    private Collection<E> collectTransitiveClosureInto(Collection<E> all) {
        for (Edge<E> each: producers.edges()) {
            Node<E> producer = each.getProducer();
            if (all.add(producer.value)) producer.collectTransitiveClosureInto(all);
        }
        return all;
    }
    
    public void addProvider(Node<E> node) {
        new Edge<E>(this, node); // adds edge to producers and consumers
    }

    public void makeBrokenEdge(Throwable error) {
        new Edge<E>(this, error); // adds broken edge to producers
    }

}
