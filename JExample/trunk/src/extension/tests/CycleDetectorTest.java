/**
 * 
 */
package extension.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;

import extension.CycleDetector;
import extension.MethodCollector;
import extension.TestClass;
import extension.TestMethod;
import extension.annotations.Depends;

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
		MethodCollector collector = new MethodCollector( new TestClass( WithoutCycles.class ),
				new HashMap<Method,TestMethod>() );
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
		MethodCollector collector = new MethodCollector( new TestClass( WithoutCyclesComplex.class ),
				new HashMap<Method,TestMethod>() );
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
		MethodCollector collector = new MethodCollector( new TestClass( WithCycles.class ),
				new HashMap<Method,TestMethod>() );
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
		MethodCollector collector = new MethodCollector( new TestClass( WithCycleOverClasses.class ),
				new HashMap<Method,TestMethod>() );
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
	public void testHasNoCyclesOverClasses() throws SecurityException, InitializationError, ClassNotFoundException,
			NoSuchMethodException {
		MethodCollector collector = new MethodCollector( new TestClass( WithoutCycleOverClasses.class ),
				new HashMap<Method,TestMethod>() );
		CycleDetector detector = new CycleDetector( collector.collectTestMethods().values() );
		assertFalse( detector.hasCycle() );
	}

	private class WithoutCycles {
		@Test
		public void rootMethod() {

		}

		@Test
		@Depends( "rootMethod" )
		public void middleMethod() {

		}

		@Test
		@Depends( "middleMethod;rootMethod" )
		public void bottomMethod() {

		}
	}

	private class WithoutCyclesComplex {

		@Test
		public void realRootMethod() {

		}

		@Test
		@Depends( "realRootMethod" )
		public void rootMethod() {

		}

		@Test
		@Depends( "rootMethod" )
		public void middleMethod() {

		}

		@Test
		@Depends( "middleMethod;rootMethod" )
		public void bottomMethod() {

		}
	}

	private class WithCycles {
		@Test
		public void rootMethod() {

		}

		@Test
		@Depends( "bottomCyclicMethod" )
		public void cyclicMethod() {

		}

		@Test
		@Depends( "rootMethod;cyclicMethod" )
		public void middleCyclicMethod() {

		}

		@Test
		@Depends( "middleCyclicMethod;rootMethod" )
		public void bottomCyclicMethod() {

		}

		@Test
		@Depends( "bottomCyclicMethod" )
		public void bottomBottomMethod() {

		}
	}

	private class WithCycleOverClasses {
		// with cycle over classes
		@Test
		public void rootMethod() {

		}

		@Test
		@Depends( "rootMethod;extension.tests.B.cyclicMethod" )
		public void middleCyclicMethod() {

		}

		@Test
		@Depends( "middleCyclicMethod;rootMethod" )
		public void bottomCyclicMethod() {

		}

		@Test
		@Depends( "bottomCyclicMethod" )
		public void bottomBottomMethod() {

		}
	}

	private class WithoutCycleOverClasses {
		// with cycle over classes
		@Test
		public void rootMethod() {

		}

		@Test
		@Depends( "rootMethod" )
		public void middleMethod() {

		}

		@Test
		@Depends( "middleMethod;B.middleMethod" )
		public void bottomCyclicMethod() {

		}
	}
}
