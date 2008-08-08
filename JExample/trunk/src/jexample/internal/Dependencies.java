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
 * 
 * @author Adrian Kuhn
 *
 */
@SuppressWarnings("serial")
public class Dependencies extends ArrayList<Example> {

    public void invalidateCycle(Example self) {
        invalidateCycle(self, new Stack<Example>(), new HashSet<Example>());
    }

    private void invalidateCycle(Example self, Stack<Example> cycle, Set<Example> done) {
        for (Example e : this) {
            cycle.push(e);
            if (e == self) for (Example $ : cycle) $.errorPartOfCycle(cycle);
            if (done.add(e)) e.providers.invalidateCycle(self, cycle, done);
            cycle.pop();
        }
    }

    public Collection<Example> transitiveClosure() {
        Collection<Example> $ = new HashSet();
        this.collectTransitiveClosureInto($);
        return $;
    }

    private void collectTransitiveClosureInto(Collection<Example> $) {
        for (Example e : this) {
            if ($.add(e)) e.providers.collectTransitiveClosureInto($);
        }
    }

    public Object[] getInjectionValues(InjectionPolicy policy, int length) throws Exception {
        Object[] $ = new Object[length];
        for (int i = 0; i < length; i++) {
            $[i] = this.get(i).returnValue.get(policy);
        }
        return $;
    }
    
    
    
}
