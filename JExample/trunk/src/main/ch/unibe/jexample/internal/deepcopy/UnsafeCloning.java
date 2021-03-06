package ch.unibe.jexample.internal.deepcopy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

import sun.reflect.ReflectionFactory;

public class UnsafeCloning implements DeepCloneStrategy {

	private static Constructor<?> makeConstructor(Class<?> type) {
		try {
			Constructor<?> constructor = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(type,
					Object.class.getConstructor());
			constructor.setAccessible(true);
			return constructor;
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		} catch (NoSuchMethodException ex) {
			throw new RuntimeException(ex);
		}
	}
	private static Collection<Field> makeFields(Class<?> type) {
		Collection<Field> fields = new ArrayList<Field>();
		for (Class<?> each = type; each != null; each = each.getSuperclass()) {
			for (Field f: each.getDeclaredFields()) {
				if (Modifier.isStatic(f.getModifiers())) continue;
				f.setAccessible(true);
				fields.add(f);
			}
		}
		return fields;
	}

	protected Collection<Field> fields;

	private Constructor<?> constructor;

	public UnsafeCloning(Class<?> type) {
		this.constructor = makeConstructor(type);
		this.fields = makeFields(type);
	}

	@Override
	public Object makeClone(Object original, CloneFactory delegate) throws Exception {
		if (original == null) return null;
		Object clone = delegate.getCachedClone(original);
		if (clone != null)	return clone;
		clone = constructor.newInstance();
		delegate.putCachedClone(original, clone);
		for (Field f: fields) f.set(clone, delegate.clone(f.get(original)));
		return clone;
	}

	@Override
	public String toString() {
		return "UnsafeCloning";
	}

}
