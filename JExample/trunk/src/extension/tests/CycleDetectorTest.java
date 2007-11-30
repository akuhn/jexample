/**
 * 
 */
package extension.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;

import extension.CycleDetector;
import extension.MethodCollector;
import extension.TestClass;
import extension.annotations.Depends;
import extension.annotations.MyTest;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class CycleDetectorTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

	}

	/**
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws InitializationError
	 * @throws SecurityException
	 */
	@Test
	public void testHasNoCycles() throws SecurityException, InitializationError, ClassNotFoundException,
			NoSuchMethodException {
		MethodCollector collector = new MethodCollector( new TestClass( WithoutCycles.class ) );
		CycleDetector detector = new CycleDetector( collector.collectTestMethods().values() );
		assertFalse( detector.hasCycle() );
	}

	/**
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws InitializationError
	 * @throws SecurityException
	 */
	@Test
	public void testHasNoCyclesComplex() throws SecurityException, InitializationError, ClassNotFoundException,
			NoSuchMethodException {
		MethodCollector collector = new MethodCollector( new TestClass( WithoutCyclesComplex.class ) );
		CycleDetector detector = new CycleDetector( collector.collectTestMethods().values() );
		assertFalse( detector.hasCycle() );
	}

	/**
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws InitializationError
	 * @throws SecurityException
	 */
	@Test
	public void testHasCycles() throws SecurityException, InitializationError, ClassNotFoundException,
			NoSuchMethodException {
		MethodCollector collector = new MethodCollector( new TestClass( WithCycles.class ) );
		CycleDetector detector = new CycleDetector( collector.collectTestMethods().values() );
		
		assertTrue( detector.hasCycle() );
	}

	/**
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws InitializationError
	 * @throws SecurityException
	 */
	@Test
	public void testHasCyclesOverClasses() throws SecurityException, InitializationError, ClassNotFoundException,
			NoSuchMethodException {
		MethodCollector collector = new MethodCollector( new TestClass( WithCycleOverClasses.class ) );
		CycleDetector detector = new CycleDetector( collector.collectTestMethods().values() );	
		assertTrue( detector.hasCycle() );
	}

//	/**
//	 * @throws NoSuchMethodException
//	 * @throws ClassNotFoundException
//	 * @throws InitializationError
//	 * @throws SecurityException
//	 */
//	@Test
//	public void testHasNoCyclesOverClasses() throws SecurityException, InitializationError, ClassNotFoundException,
//			NoSuchMethodException {
//		CycleDetector detector = new CycleDetector( new TestClass( WithoutCycleOverClasses.class ) );
//		assertEquals( 4, detector.checkCyclesAndGetMethods().size() );
//	}

	private class WithoutCycles {
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
	}

	private class WithoutCyclesComplex {

		@MyTest
		public void realRootMethod() {

		}

		@MyTest
		@Depends( "realRootMethod" )
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
	}

	private class WithCycles {
		@MyTest
		public void rootMethod() {

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

		@MyTest
		@Depends( "bottomCyclicMethod" )
		public void bottomBottomMethod() {

		}
	}

	private class WithCycleOverClasses {
		// with cycle over classes
		@MyTest
		public void rootMethod() {

		}

		@MyTest
		@Depends( "rootMethod;extension.tests.B.cyclicMethod" )
		public void middleCyclicMethod() {

		}

		@MyTest
		@Depends( "middleCyclicMethod;rootMethod" )
		public void bottomCyclicMethod() {

		}

		@MyTest
		@Depends( "bottomCyclicMethod" )
		public void bottomBottomMethod() {

		}
	}

	private class WithoutCycleOverClasses {
		// with cycle over classes
		@MyTest
		public void rootMethod() {

		}

		@MyTest
		@Depends( "rootMethod" )
		public void middleMethod() {

		}

		@MyTest
		@Depends( "middleMethod;B.middleMethod" )
		public void bottomCyclicMethod() {

		}
	}
}
