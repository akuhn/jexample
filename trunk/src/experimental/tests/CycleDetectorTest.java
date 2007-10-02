/**
 * 
 */
package experimental.tests;

import static org.junit.Assert.assertFalse;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import experimental.CycleDetector;
import experimental.TestMethod;
import extension.annotations.Depends;
import extension.annotations.MyTest;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class CycleDetectorTest {

	private HashSet<TestMethod> methodsWithoutCycle;

	private TestMethod rootMethod, middleMethod, bottomMethod;
	
	private TestMethod cyclicMethod, middleCyclicMethod, bottomCyclicMethod;

	private Set<TestMethod> methodsWithCycle;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.methodsWithoutCycle = new HashSet<TestMethod>();
		this.methodsWithCycle = new HashSet<TestMethod>();

		this.rootMethod = new TestMethod( this.getClass().getMethod( "rootMethod" ) );
		this.middleMethod = new TestMethod( this.getClass().getMethod( "middleMethod" ) );
		this.middleMethod.addDependency( this.rootMethod );
		this.bottomMethod = new TestMethod( this.getClass().getMethod( "bottomMethod" ) );
		this.bottomMethod.addDependency( this.middleMethod );
		this.bottomMethod.addDependency( this.rootMethod );

		this.methodsWithoutCycle.add( this.rootMethod );
		this.methodsWithoutCycle.add( this.middleMethod );
		this.methodsWithoutCycle.add( this.bottomMethod );

		this.cyclicMethod = new TestMethod( this.getClass().getMethod( "cyclicMethod" ) );
		this.bottomCyclicMethod = new TestMethod( this.getClass().getMethod( "bottomMethod" ) );
		this.middleCyclicMethod = new TestMethod( this.getClass().getMethod( "middleCyclicMethod" ) );
		
		this.cyclicMethod.addDependency( this.bottomCyclicMethod );
		
		this.middleCyclicMethod.addDependency( this.rootMethod );
		this.middleCyclicMethod.addDependency( this.cyclicMethod );
		
		this.bottomCyclicMethod.addDependency( this.middleCyclicMethod );
		this.bottomCyclicMethod.addDependency( this.rootMethod );
		
		this.methodsWithCycle.add( this.rootMethod );
		this.methodsWithCycle.add( this.cyclicMethod );
		this.methodsWithCycle.add( this.middleCyclicMethod );
		this.methodsWithCycle.add( this.bottomCyclicMethod );
	}

	/**
	 * Test method for {@link experimental.CycleDetector#testHasNoCycles()}.
	 */
	@Test
	public void testHasNoCycles() {
		CycleDetector detector = new CycleDetector( this.methodsWithoutCycle );
		assertFalse( detector.hasCycles() );
	}
	
	/**
	 * Test method for {@link experimental.CycleDetector#testHasCycles()}.
	 */
	@Test
	public void testHasCycles() {
		CycleDetector detector = new CycleDetector( this.methodsWithCycle );
		assertFalse( detector.hasCycles() );
	}

	@MyTest
	public void rootMethod() {

	}

	@MyTest
	@Depends( "rootMethod" )
	public void middleMethod() {

	}

	@MyTest
	@Depends( "middleMethod;rootMethod" )
	public void bottomMethod() {

	}

	@MyTest
	@Depends( "bottomCyclicMethod" )
	public void cyclicMethod() {

	}

	@MyTest
	@Depends( "rootMethod;cyclicMethod" )
	public void middleCyclicMethod() {

	}

	@MyTest
	@Depends( "middleCyclicMethod;rootMethod" )
	public void bottomCyclicMethod() {

	}
}
