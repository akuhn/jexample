package ch.unibe.jexample.internal.tests;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.io.Serializable;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.Injection;
import ch.unibe.jexample.InjectionPolicy;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
@Injection(InjectionPolicy.DEEPCOPY)
public class DeepCloneSerializableObjects implements Serializable {

	private static final long serialVersionUID = 1L;

	static DeepCloneSerializableObjects original;
	
	Object[] a, b;
	
	@Test
	public void first() {
		a = new Object[1];
		b = new Object[1];
		a[0] = b;
		b[0] = a;
		original = this;
		assertSame(this.a, this.b[0]);
		assertSame(this.b, this.a[0]);
	}
	
	@Given("#first")
	public void then() {
		assertNotSame(original, this);
		assertSame(this.a, this.b[0]);
		assertSame(this.b, this.a[0]);
	}
		
}
