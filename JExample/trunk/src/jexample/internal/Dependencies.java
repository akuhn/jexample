package jexample.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jexample.InjectionPolicy;

public class Dependencies extends ArrayList<TestMethod> {

    public Collection<TestMethod> transitiveClosure() {
        Collection<TestMethod> $ = new ArrayList();
        this.collectTransitiveClosureInto($);
        return $;
    }

    private void collectTransitiveClosureInto(Collection<TestMethod> $) {
        $.addAll(this);
        for (TestMethod p : this)
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
