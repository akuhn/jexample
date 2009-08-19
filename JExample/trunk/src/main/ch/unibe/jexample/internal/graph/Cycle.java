package ch.unibe.jexample.internal.graph;

import java.util.Collection;

public class Cycle<E> {

    private Edge<?>[] cycle;

    public Cycle(Collection<Edge<E>> cycle) {
        this.cycle = cycle.toArray(new Edge<?>[cycle.size()]);
    }
    
    public int length() {
        return cycle.length;
    }
    
}
