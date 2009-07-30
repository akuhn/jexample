package ch.unibe.jexample.deepclone;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;

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

	public static boolean DEBUG_TRACE = false;
	private static ThreadLocal<Stack<String>> debugTraceStack = new ThreadLocal<Stack<String>>();
	
	private Map<Object,Object> done = new IdentityHashMap<Object,Object>();
	private DeepCloneStrategyCache cache = DeepCloneStrategyCache.getDefault();

	@SuppressWarnings("unchecked")
	public <T> T clone(T original) throws DeepCloneException {
		try {
			if (original == null) return null;
			debugPush(original);
			T clone = (T) cache.lookup(original).makeClone(original, this);
			debugPop();
			return clone;
		} catch (DeepCloneException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new DeepCloneException(debugTraceStack, ex);
		}
	}

	private void debugPush(Object original) {
		if (!DEBUG_TRACE) return;
		if (debugTraceStack.get() == null) debugTraceStack.set(new Stack<String>());
		debugTraceStack.get().push(original.getClass().toString());
	}
	
	private void debugPop() {
		if (!DEBUG_TRACE) return;
		if (debugTraceStack.get() == null) debugTraceStack.set(new Stack<String>());
		debugTraceStack.get().pop();
	}

	public static <T> T deepClone(T object) {
		return new CloneFactory().clone(object);
	}

	public Object getCachedClone(Object original) {
		return done.get(original);
	}

	public void putCachedClone(Object original, Object clone) {
		done.put(original, clone);
	}

}
