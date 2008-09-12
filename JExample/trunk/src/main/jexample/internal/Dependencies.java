package jexample.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import jexample.InjectionPolicy;

/**
 * Manages the dependencies/providers of an example.
 * 
 * @author Adrian Kuhn
 *
 */
@SuppressWarnings("serial")
public class Dependencies extends ArrayList<Example> {

    /** Checks if these dependencies are part of a cycle.
     * For each detected cycle, invalidates all contained nodes.
     * 
     * @param $ consumer of these dependencies.
     */
    public void validateCycle(Example $) {
        validateCycle($, new Stack<Example>(), new HashSet<Example>());
    }

    private void validateCycle(Example $, Stack<Example> cycle, Set<Example> done) {
        for (Example e: this) {
            cycle.push(e);
            if ($ == e) invalidate(cycle);
            if (done.add(e)) e.providers.validateCycle($, cycle, done);
            cycle.pop();
        }
    }

    private void invalidate(Stack<Example> cycle) {
        for (Example each: cycle) each.errorPartOfCycle(cycle);
    }

    public Collection<Example> transitiveClosure() {
        Collection<Example> all = new HashSet();
        this.collectTransitiveClosureInto(all);
        return all;
    }

    private void collectTransitiveClosureInto(Collection<Example> all) {
        for (Example e : this) {
            if (all.add(e)) e.providers.collectTransitiveClosureInto(all);
        }
    }

    public Object[] getInjectionValues(InjectionPolicy policy, int length) throws Exception {
        Object[] values = new Object[length];
        for (int i = 0; i < length; i++) {
            values[i] = this.get(i).returnValue.get(policy);
        }
        return values;
    }
    
    
    
}
