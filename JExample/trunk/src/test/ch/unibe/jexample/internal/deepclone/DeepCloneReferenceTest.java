package ch.unibe.jexample.internal.deepclone;

import static org.junit.Assert.assertEquals;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import org.junit.Test;

import ch.unibe.jexample.internal.deepcopy.CloneFactory;

@SuppressWarnings("unchecked")
public class DeepCloneReferenceTest {

	private Object dummy;
	private Reference ref;

	@Test
	public void testWeakReference() {
		dummy = new Object();
		ref = new WeakReference(dummy);
		testReference();
	}

	@Test
	public void testSoftReference() {
		dummy = new Object();
		ref = new SoftReference(dummy);
		testReference();
	}

	@Test
	public void testPhantomReference() {
		dummy = new Object();
		ref = new PhantomReference(dummy, null);
		assertEquals(null, ref.get());

		CloneFactory cf = new CloneFactory();
		ref = cf.clone(ref);
		dummy = cf.clone(dummy);
		
		assertEquals(null, ref.get());
	}
	
	
	private void testReference() {
		assertEquals(dummy, ref.get());

		CloneFactory cf = new CloneFactory();
		ref = cf.clone(ref);
		dummy = cf.clone(dummy);
		
		assertEquals(dummy, ref.get());
	}
	
	
}
