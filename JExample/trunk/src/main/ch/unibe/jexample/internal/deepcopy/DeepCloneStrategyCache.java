package ch.unibe.jexample.internal.deepcopy;

import static ch.unibe.jexample.internal.deepcopy.DeepCloneStrategy.IMMUTABLE;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class DeepCloneStrategyCache {

    private static DeepCloneStrategyCache DEFAULT = null;

    public static DeepCloneStrategyCache getDefault() {
        return DEFAULT == null ? DEFAULT = new DeepCloneStrategyCache() : DEFAULT;
    } 
    public Map<Class<?>,DeepCloneStrategy> cache;

    public DeepCloneStrategyCache() {
        this.cache = new IdentityHashMap<Class<?>,DeepCloneStrategy>();
    }

    public DeepCloneStrategy lookup(Class<?> type) {
        if (type.isPrimitive()) return IMMUTABLE;
        DeepCloneStrategy result = cache.get(type);
        if (result != null) return result;
        cache.put(type, result = makeStrategy(type));
        return result;
    }

    /** Detects pseudo enums. We assume that a class is a pseudo enum
     * iff it
     * 		is final,
     * 		directly extends Object,
     * 		has a private String field called name,
     * 		does not have a #setName method,
     * 		has zero or more public static final fields of its own type,
     * 		and optionally a serial version field.
     * 
     */
    private boolean isPseudoEnum(Class<?> type) {
        if (!Modifier.isFinal(type.getModifiers())) return false;
        if (!(type.getSuperclass() == Object.class)) return false;
        boolean hasName = false;
        for (Field f: type.getDeclaredFields()) {
            if (f.getName().equals("serialVersionUID")) continue;
            int mod = f.getModifiers();
            if (Modifier.isStatic(mod)) {
                if (!Modifier.isPublic(mod)) return false;
                if (!Modifier.isFinal(mod)) return false;
                if (f.getType() != type) return false;
            }
            else {
                if (!Modifier.isPrivate(mod)) return false;
                if (f.getType() != String.class) return false;
                if (!f.getName().equals("name")) return false;
                hasName = true;
            }
        }
        if (!hasName) return false;
        for (java.lang.reflect.Method m: type.getDeclaredMethods()) {
            if (m.getName().equals("setName")) return false;    		
        }
        return true;
    }

    public DeepCloneStrategy lookup(Object object) {
        return lookup(object.getClass());
    }

    private DeepCloneStrategy makeStrategy(Class<?> type) {
        if (type.isArray()) return new ArrayCloning(type);
        if (ImmutableClasses.contains(type)) return IMMUTABLE;
        if (HashMap.class.isAssignableFrom(type)) return new HashMapCloning(); 
        if (Reference.class.isAssignableFrom(type)) return new UnsafeWithoutTransientCloning(type);
        if (isPseudoEnum(type)) return IMMUTABLE;
        return new UnsafeCloning(type);
    }

}
