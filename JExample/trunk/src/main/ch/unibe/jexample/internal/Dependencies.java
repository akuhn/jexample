package ch.unibe.jexample.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import ch.unibe.jexample.JExampleOptions;

/**
 * Manages the dependencies/providers of an example.
 * 
 * @author Adrian Kuhn
 * 
 */
@SuppressWarnings("serial")
public class Dependencies extends ArrayList<Example> {

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
        for (Example e: this) {
            if (all.add(e)) e.providers.collectTransitiveClosureInto(all);
        }
    }

    public Object[] getInjectionValues(JExampleOptions policy, int length) throws Exception {
        Object[] values = new Object[length];
        for (int i = 0; i < length; i++) {
            values[i] = this.get(i).returnValue.get(policy);
        }
        return values;
    }

    public boolean hasFirstProviderImplementedIn(Example example) {
        return !isEmpty() && get(0).returnValue.hasTestCaseInstance(example.method.jclass);
    }

}
