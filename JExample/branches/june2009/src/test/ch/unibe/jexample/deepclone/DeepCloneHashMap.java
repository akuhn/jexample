package ch.unibe.jexample.deepclone;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class DeepCloneHashMap {

	@Test
	public void test() {
		Map map = new HashMap();
		HashDummy dummy = new HashDummy();
		map.put(dummy, "Marblecake");
		
		assertTrue(map.containsKey(dummy));
		
		CloneFactory cf = new CloneFactory();
		map = cf.clone(map);
		dummy = cf.clone(dummy);
		
		assertTrue(map.containsKey(dummy));
		
	}
	
	static class HashDummy {
		/* We are crash test dummies. */
	}
	
}
