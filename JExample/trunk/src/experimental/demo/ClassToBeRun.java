/**
 * 
 */
package experimental.demo;

import org.junit.runner.RunWith;

import extension.ComposedTestRunner;
import extension.annotations.Depends;
import extension.annotations.MyTest;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
@RunWith( ComposedTestRunner.class )
public class ClassToBeRun {

	public ClassToBeRun() {
	}

	@MyTest
	public void rootMethod() {
		System.out.println( "I have to be run first." );
	}

	@MyTest
	@Depends( "middleMethod" )
	public void bottomMethod() {
		System.out.println( "I have to be run as a leaf of the graph." );
	}

	@MyTest
	@Depends( "SomeDependencies.middleMethodDeps" )
	public void secondBottomMethod() {
		System.out.println( "I have to be run as a leaf of the graph, too." );
	}

	@MyTest
	@Depends( "rootMethod" )
	public void middleMethod() {
		System.out.println( "I'm run in the middle, I'm in ClassToBeRun." );
	}
}