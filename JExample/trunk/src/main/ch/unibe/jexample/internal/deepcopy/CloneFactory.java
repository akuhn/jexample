package ch.unibe.jexample.internal.deepcopy;

import java.util.IdentityHashMap;
import java.util.Map;

/** Deep-clones any object. Uses unsafe reflection to clone any objects, no matter whether cloneable or not.
 * Does not call any methods, neither constructors nor accessors nor <code>clone</code> methods.
 *<p>
 * <i>Fluent naming convention:</i> Instances of this class should be named <tt>deep</tt>, in order to complete the fluent naming
 * of the <tt>deep.clone()</tt> method.
 * 
 * @author Adrian Kuhn
 *
 */
public class CloneFactory {

    public static <T> T deepClone(T object) {
        return new CloneFactory().clone(object);
    }
    private DeepCloneStrategyCache cache = DeepCloneStrategyCache.getDefault();

    private Map<Object,Object> done = new IdentityHashMap<Object,Object>();

    @SuppressWarnings("unchecked")
    public <T> T clone(T original) throws DeepCloneException {
        try {
            if (original == null) return null;
            return (T) cache.lookup(original).makeClone(original, this);
        } catch (DeepCloneException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new DeepCloneException(ex);
        }
    }

    public Object getCachedClone(Object original) {
        return done.get(original);
    }

    public void putCachedClone(Object original, Object clone) {
        done.put(original, clone);
    }

}
