package ch.unibe.jexample.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/** Incoming dependencies of an example.
 *<P> 
 * @author Adrian Kuhn
 *
 */
public class Producers implements Iterable<Example> {

    private List<Example> producers;
    private int arity = 0;
    
    public Producers(int arity, Example... examples) {
        this.arity = arity;
        this.producers = new ArrayList<Example>();
        for (Example each: examples) producers.add(each);
    }
    
    public Producers(int arity, Collection<Example> examples) {
        this.arity = arity;
        this.producers = new ArrayList<Example>(examples);
    }

    @Override
    public Iterator<Example> iterator() {
        return Collections.unmodifiableCollection(producers).iterator();
    }

    public int arity() {
        return arity;
    }

    public int size() {
        return producers.size();
    }

    public boolean isEmpty() {
        return producers.isEmpty();
    }

    public Example get(int index) {
        return producers.get(index);
    }

    public Object first() {
        return producers.get(0);
    }
    
}
