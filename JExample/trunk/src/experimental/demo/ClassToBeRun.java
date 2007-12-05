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
public class ClassToBeRun {

	public ClassToBeRun() {
	}

	@Test
	public void rootMethod() {
		System.out.println( "I have to be run first." );
	}

	@Test
	@Depends( "middleMethod" )
	public void bottomMethod() {
		System.out.println( "I have to be run as a leaf of the graph." );
	}

	@Test
	@Depends( "SomeDependencies.middleMethodDeps" )
	public void secondBottomMethod() {
		System.out.println( "I have to be run as a leaf of the graph, too." );
	}

	@Test
	@Depends( "rootMethod" )
	public void middleMethod() {
		System.out.println( "I'm run in the middle, I'm in ClassToBeRun." );
	}
}