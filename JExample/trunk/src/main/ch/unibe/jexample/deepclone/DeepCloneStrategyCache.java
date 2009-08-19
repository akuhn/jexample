package ch.unibe.jexample.deepclone;

import static ch.unibe.jexample.deepclone.DeepCloneStrategy.IMMUTABLE;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;



public class DeepCloneStrategyCache {

    private static final String[] IMMUTABLES = {
        "sun.font", // package
        "java.lang.Boolean",
        "java.lang.Character",
        "java.lang.Void",
        "java.lang.String",
        "java.lang.Number",
        "java.lang.Class",
        "java.lang.ClassLoader",
        "java.lang.Throwable",
        "java.lang.Thread",
        // TODO more...
    };

    private static DeepCloneStrategyCache DEFAULT = null;

    public Map<Class<?>,DeepCloneStrategy> cache; 
    public Map<Object,Void> constants;

    private boolean isImmutable(Class<?> type) {
        if (type.isEnum()) return true;
        if (type.isAnnotation()) return true;
        if (type.isPrimitive()) return true;
        if (Object.class == type) return true;
        for (Class<?> curr = type; curr != null; curr = curr.getSuperclass()) {
            String fqn = curr.getName();
            for (String each: IMMUTABLES) if (fqn.startsWith(each)) return true;
        }
        return false;
    }

    public static DeepCloneStrategyCache getDefault() {
        return DEFAULT == null ? DEFAULT = new DeepCloneStrategyCache() : DEFAULT;
    }

    public DeepCloneStrategyCache() {
        this.cache = new IdentityHashMap<Class<?>,DeepCloneStrategy>();
        this.constants = new IdentityHashMap<Object,Void>();
    }

    public DeepCloneStrategy lookup(Object object) {
        return lookup(object.getClass());
    }

    public boolean isConstant(Object object) {
        return constants.containsKey(object);
    }
    
    public DeepCloneStrategy lookup(Class<?> type) {
        if (type.isPrimitive()) return IMMUTABLE;
        DeepCloneStrategy result = cache.get(type);
        if (result != null) return result;
        cache.put(type, result = makeStrategy(type));
        if (result != IMMUTABLE) addConstants(type);
        return result;
    }

    private void addConstants(Class<?> type) {
        for (Class<?> curr = type; curr != null; curr = curr.getSuperclass()) {
            for (Field f: curr.getDeclaredFields()) {
                if (Modifier.isFinal(f.getModifiers()) && Modifier.isStatic(f.getModifiers())) {
                    f.setAccessible(true);
                    Object value;
                    try {
                        value = f.get(null);
                        if (value != null) constants.put(value, null);
                    } catch (IllegalArgumentException ex) {
                        throw new DeepCloneException(ex);
                    } catch (IllegalAccessException ex) {
                        throw new DeepCloneException(ex);
                    } 
                }
            }
        }
    }

    private DeepCloneStrategy makeStrategy(Class<?> type) {
        if (isImmutable(type)) return IMMUTABLE;
        if (type.isArray()) return new ArrayCloning(type);
        if (HashMap.class.isAssignableFrom(type)) return new HashMapCloning(); 
        if (Reference.class.isAssignableFrom(type)) return new UnsafeWithoutTransientCloning(type);
        if (noFieldsOrFinalFieldsOnly(type)) return IMMUTABLE;
        return new UnsafeCloning(type);
    }

    private boolean noFieldsOrFinalFieldsOnly(Class<?> type) {
        for (Class<?> curr = type; curr != null; curr = curr.getSuperclass()) {
            for (Field f: curr.getDeclaredFields()) {
                if (!Modifier.isFinal(f.getModifiers())) return false;
                if (!isImmutable(f.getType())) return false;
            }
        }
        return true;
    }


}
