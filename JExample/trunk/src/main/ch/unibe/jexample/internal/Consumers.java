package ch.unibe.jexample.internal;

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
public class Consumers implements Iterable<Example> {

    private List<Example> consumers;
    
    public Consumers(Example... examples) {
        this.consumers = new ArrayList<Example>();
        for (Example each: examples) consumers.add(each);
    }
    
    public Consumers(Collection<Example> examples) {
        this.consumers = new ArrayList<Example>(examples);
    }

    @Override
    public Iterator<Example> iterator() {
        return Collections.unmodifiableCollection(consumers).iterator();
    }

    public int size() {
        return consumers.size();
    }
    
}
