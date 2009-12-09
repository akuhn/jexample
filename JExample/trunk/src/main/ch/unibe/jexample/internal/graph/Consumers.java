package ch.unibe.jexample.internal.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/** Outgoing dependencies of an example.
 * 
 * @author akuhn
 *
 */
public class Consumers<E> implements Iterable<E> {

    private List<Edge<E>> edges = new ArrayList<Edge<E>>();
    
    public List<Edge<E>> edges() {
        return Collections.unmodifiableList(edges);
    }

    public E first() {
        return value(edges.get(0));
    }

    public E get(int index) {
        return value(edges.get(index));
    }

    public boolean isEmpty() {
        return edges.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        Collection<E> iterable = new ArrayList<E>(edges.size());
        for (Edge<E> edge: edges) iterable.add(value(edge));
        return iterable.iterator();
    }

    public int size() {
        return edges.size();
    }

    protected void add(Edge<E> edge) {
        edges.add(edge);
    }
    
    protected E value(Edge<E> edge) {
       return edge.consumer.value;
    }
    
    @Override
    public String toString() {
        Collection<E> iterable = new ArrayList<E>(edges.size());
        for (Edge<E> edge: edges) iterable.add(value(edge));
        return iterable.toString();
    }
    
}
