package ch.unibe.jexample.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import ch.unibe.jexample.JExampleOptions;

/**
 * Manages the dependencies/providers of an example.
 * 
 * @author Adrian Kuhn
 * 
 */
public class Dependencies implements Iterable<Example> {

    private List<Dependency> elements = new ArrayList<Dependency>();
    
    /**
     * Checks if these dependencies are part of a cycle. For each detected
     * cycle, invalidates all contained nodes.
     * 
     * @param example
     *            consumer of these dependencies.
     */
    public void validateCycle(Example example) {
        validateCycle(example, new Stack<Example>(), new HashSet<Example>());
    }

    private void validateCycle(Example example, Stack<Example> cycle, Set<Example> done) {
        for (Example each: this) {
            cycle.push(each);
            if (example == each) invalidate(cycle);
            if (done.add(each)) each.providers.validateCycle(example, cycle, done);
            cycle.pop();
        }
    }

    private void invalidate(Stack<Example> cycle) {
        for (Example each: cycle)
            each.errorPartOfCycle(cycle);
    }

    public Collection<Example> transitiveClosure() {
        Collection<Example> all = new HashSet<Example>();
        this.collectTransitiveClosureInto(all);
        return all;
    }

    private void collectTransitiveClosureInto(Collection<Example> all) {
        for (Example each: this) {
            if (all.add(each)) each.providers.collectTransitiveClosureInto(all);
        }
    }

    public Object[] getInjectionValues(JExampleOptions policy, int length) throws Exception {
        Object[] values = new Object[length];
        for (int i = 0; i < length; i++) {
            values[i] = elements.get(i).dependency().returnValue.get(policy);
        }
        return values;
    }

    public boolean hasFirstProviderImplementedIn(Example example) {
        return !elements.isEmpty() && first().returnValue.hasTestCaseInstance(example.method.jclass);
    }

    public Example first() {
        return elements.get(0).dependency();
    }

    public int size() {
        return elements.size();
    }

    @Override
    public Iterator<Example> iterator() {
        return new Iter(elements.iterator());
    }

    public void add(Example d) {
        elements.add(new Dependency(d));
    }

    private static class Iter implements Iterator<Example> {
     
        private Iterator<Dependency> it;
        
        public Iter(Iterator<Dependency> it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Example next() {
            return it.next().dependency();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }
    
}
