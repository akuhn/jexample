package ch.unibe.jexample.internal.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Cycle<E> {

    private List<Dependency<E>> cycle;

    public Cycle(Collection<Dependency<E>> cycle) {
        this.cycle = new ArrayList<Dependency<E>>(cycle);
    }
    
    public int length() {
        return cycle.size();
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("(");
        if (!cycle.isEmpty()) buf.append(cycle.get(0).consumer.value);
        for (Dependency<?> each: cycle) buf.append(" < ").append(each.producer.value);
        buf.append(")");
        return buf.toString();
    }
    
}
