package ch.unibe.jexample.internal.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Cycle<E> {

    private List<Edge<E>> cycle;

    public Cycle(Collection<Edge<E>> cycle) {
        this.cycle = new ArrayList<Edge<E>>(cycle);
    }
    
    public int length() {
        return cycle.size();
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("(");
        if (!cycle.isEmpty()) buf.append(cycle.get(0).consumer.value);
        for (Edge<?> each: cycle) buf.append(" < ").append(each.producer.value);
        buf.append(")");
        return buf.toString();
    }
    
}
