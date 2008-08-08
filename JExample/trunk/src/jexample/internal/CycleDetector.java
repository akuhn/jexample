package jexample.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 
 * @author Adrian Kuhn
 *
 * @param <E>
 */
public abstract class CycleDetector<E> {

    private class Node {

        public int color = 0;
        
        public final E payload;
        
        public Node(E payload) {
            this.payload = payload;
        }
        
        private final boolean isWhite() {
            return color == 0;
        }
        
        private final void beGray() {
            color = 1;
        }
        
        private final void beBlack() {
            color = 2;
        }
        
        private final void cycle() throws CycleFound {
            if (!isWhite()) return;
            beGray();
            path.push(this.payload);
            for (Node n : children()) {
                if (n.isGray()) throw new CycleFound();
                if (n.isWhite()) n.cycle();
            }
            path.pop();
            beBlack();
        }

        private final boolean isGray() {
            return color == 1;
        }

        private final Collection<Node> children() {
            Collection<E> es = getChildren(payload);
            Collection<Node> ns = new ArrayList(es.size());
            for (E e : es) {
                Node n = map.get(e);
                assert n != null;
                ns.add(n);
            }
            return ns;
        }
        
    }
    
    private Stack<E> path = new Stack();
    private Map<E, Node> map = new HashMap();

    public CycleDetector() {
    }
    
    public CycleDetector(Collection<E>... ess) {
        for (Collection<E> es : ess)
            for (E e : es)
                put(e);
    }
    
    public CycleDetector<E> put(E e) {
        map.put(e, new Node(e));
        return this;
    }
    
    public abstract Collection<E> getChildren(E payload);

    public List<E> getCycle() {
        try {
            for (Node n : map.values()) n.cycle();
        }
        catch (CycleFound ex) {
            return path;
        }
        return null;
    }
    
    @SuppressWarnings("serial")
    private static class CycleFound extends Exception {
        
    }

    public boolean hasCycle() {
        return getCycle() != null;
    }
    
}