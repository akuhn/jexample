package jexample.internal;

import java.util.ArrayList;
import java.util.Collection;

import jexample.InjectionPolicy;

/**
 * 
 * @author Adrian Kuhn
 *
 */
@SuppressWarnings("serial")
public class Dependencies extends ArrayList<Example> {

    public Collection<Example> transitiveClosure() {
        Collection<Example> $ = new ArrayList();
        this.collectTransitiveClosureInto($);
        return $;
    }

    private void collectTransitiveClosureInto(Collection<Example> $) {
        $.addAll(this);
        for (Example p : this)
            p.providers.collectTransitiveClosureInto($);
    }
    

    public Object[] getInjectionValues(InjectionPolicy policy, int length) throws Exception {
        Object[] $ = new Object[length];
        for (int i = 0; i < length; i++) {
            $[i] = this.get(i).returnValue.get(policy);
        }
        return $;
    }
    
}
