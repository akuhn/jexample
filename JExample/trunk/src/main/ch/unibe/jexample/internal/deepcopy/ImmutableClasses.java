package ch.unibe.jexample.internal.deepcopy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ImmutableClasses {

    private static final ImmutableClasses DEFAULT = new ImmutableClasses();
    
    public static boolean contains(Class<?> type) {
        return DEFAULT.isImmutable(type);
    }
    
    private Map<Class<?>,Boolean> cache = new HashMap<Class<?>,Boolean>();

    public boolean isImmutable(Class<?> type) {
        Boolean bool = cache.get(type);
        if (bool == null) cache.put(type, bool = isImmutable0(type));
        return bool.booleanValue();
    }

    private boolean isImmutable0(Class<?> type) {
        if (type.isEnum()) return true;
        if (type.isAnnotation()) return true;
        if (type.isPrimitive()) return true;
        if (Object.class == type) return true;
        for (Class<?> curr = type; curr != null; curr = curr.getSuperclass()) {
            String fqn = curr.getName();
            for (String each: PATTERNS) if (match(each, fqn)) return true;
        }
        return noFieldsOrFinalFieldsOnly(type);
    }

    private boolean noFieldsOrFinalFieldsOnly(Class<?> type) {
        for (Class<?> curr = type; curr != null; curr = curr.getSuperclass()) {
            for (Field f: curr.getDeclaredFields()) {
                if (!Modifier.isFinal(f.getModifiers())) return false;
                if (!isImmutable0(f.getType())) return false;
            }
        }
        return true;
    }
    
    private static final String[] PATTERNS = {
        "java.lang.Boolean",
        "java.lang.Character",
        "java.lang.Void",
        "java.lang.String",
        "java.lang.Number",
        "java.lang.Class",
        "java.lang.ClassLoader",
        "java.lang.Throwable",
        "java.lang.Thread",
        "sun.font.*", // caused illegal memory access
    };    

    public static boolean match(String pattern, String string) {
        return pattern.endsWith("*")
                ? string.startsWith(pattern.substring(0, pattern.length() - 1))
                : string.equals(pattern);
    }
    
}
