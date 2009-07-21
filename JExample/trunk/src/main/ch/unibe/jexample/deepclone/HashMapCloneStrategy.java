package ch.unibe.jexample.deepclone;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("unchecked")
public class HashMapCloneStrategy implements DeepCloneStrategy {

	@Override
	public Object makeClone(Object original, CloneFactory delegate) throws Exception {
		HashMap<?,?> map = (HashMap) original;
		HashMap clone = map.getClass().newInstance();
		for (Map.Entry each: map.entrySet()) {
			Object key = delegate.clone(each.getKey());
			Object value = delegate.clone(each.getValue());
			clone.put(key, value);
		}
		return clone;
	}

	public static DeepCloneStrategy getDefault() {
		// dont cache singelton for the moment to save perm space
		return new HashMapCloneStrategy(); 
	}

}
