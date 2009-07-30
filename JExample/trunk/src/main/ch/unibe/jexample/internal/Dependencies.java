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
public class Dependencies implements Iterable<Dependency> {

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
        for (Dependency each: elements) {
            if (each.isBroken()) continue;
            Example eg = each.dependency();
            cycle.push(eg);
            if (example == eg) invalidate(cycle);
            if (done.add(eg)) eg.providers.validateCycle(example, cycle, done);
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
        for (Dependency each: this) {
            if (each.isBroken()) continue;
            Example eg = each.dependency();
            if (all.add(eg)) eg.providers.collectTransitiveClosureInto(all);
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
    public Iterator<Dependency> iterator() {
        return elements.iterator();
    }

    public void add(Example producer, Example consumer) {
        elements.add(new Dependency(producer, consumer));
    }

    public void addBroken(Throwable error) {
        elements.add(new Dependency(error));
    }
    
    
    
}
