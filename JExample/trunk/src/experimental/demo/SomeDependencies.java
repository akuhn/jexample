/**
 * 
 */
package experimental.demo;

import org.junit.Test;
import org.junit.runner.RunWith;

import extension.ComposedTestRunner;
import extension.annotations.Depends;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
@RunWith( ComposedTestRunner.class )
public class SomeDependencies {

	public SomeDependencies() {
	}

	@Test
	@Depends( "SomeMoreDependencies.middleMethodMoreDeps" )
	public void middleMethodDeps() {
		System.out.println( "I'm run in the middle, I'm in SomeDependencies." );
	}
}
