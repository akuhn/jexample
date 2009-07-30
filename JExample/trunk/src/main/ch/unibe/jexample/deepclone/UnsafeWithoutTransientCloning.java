package ch.unibe.jexample.deepclone;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

public class UnsafeWithoutTransientCloning extends UnsafeCloning {

	public UnsafeWithoutTransientCloning(Class<?> type) {
		super(type);
		Iterator<Field> it = fields.iterator();
		while (it.hasNext()) if (Modifier.isTransient(it.next().getModifiers())) it.remove();
	}

	@Override
	public String toString() {
		return "UnsafeCloning without transient fields.";
	}


}
