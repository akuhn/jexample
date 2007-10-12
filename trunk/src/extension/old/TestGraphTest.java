/**
 * 
 */
package extension.old;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;

import extension.annotations.Depends;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class TestGraphTest {

	private DependencyGraph graph;

	private List<MyTestMethod> methods;

	private MyTestClass testClass;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.methods = new ArrayList<MyTestMethod>();
		testClass = new MyTestClass( this.getClass() );
		this.methods.add( new MyTestMethod( this.getClass().getMethod( "rootMethod" ), testClass ) );
		this.methods.add( new MyTestMethod( this.getClass().getMethod( "middleMethod" ), testClass ) );
		this.methods.add( new MyTestMethod( this.getClass().getMethod( "bottomMethod" ), testClass ) );

		this.graph = new DependencyGraph( this.methods, new MyTestClass( this.getClass() ) );
	}

	@Test
	public void testCreateGraph() {
		assertEquals( 3, this.graph.getNodes().size() );
	}

	@Test
	public void testRootHasNoParent() throws SecurityException, NoSuchMethodException, ClassNotFoundException, InitializationError {
		MyTestMethod node = this.graph.getEqualNode( new MyTestMethod(  this.getClass().getMethod( "rootMethod" ), testClass ) );
		assertEquals( 0, node.getParents().size() );
	}

	@Test
	public void testBottomHasParents() throws SecurityException, NoSuchMethodException, ClassNotFoundException, InitializationError {
		MyTestMethod node = this.graph.getEqualNode( new MyTestMethod( this.getClass().getMethod( "bottomMethod" ), testClass ) );
		assertEquals( 2, node.getParents().size() );
	}

	@Test
	public void testMiddleHasParentsAndChildren() throws SecurityException, NoSuchMethodException, ClassNotFoundException, InitializationError {
		MyTestMethod node = this.graph.getEqualNode( new MyTestMethod( this.getClass().getMethod( "middleMethod" ), testClass ) );
		assertEquals( 1, node.getParents().size() );

		// assertEquals( 1, node.getChildren().size() );
	}

	@Test
	public void testSort() throws SecurityException, NoSuchMethodException, ClassNotFoundException, InitializationError {
		List<MyTestMethod> sorted = this.graph.getSortedNodes();

		assertEquals( new MyTestMethod( this.getClass().getMethod( "rootMethod" ), testClass ) , sorted.get( 0 ) );
		assertEquals( new MyTestMethod( this.getClass().getMethod( "middleMethod" ), testClass ) , sorted.get( 1 ) );
		assertEquals( new MyTestMethod( this.getClass().getMethod( "bottomMethod" ), testClass ) , sorted.get( 2 ) );
	}

	@Test( expected = GraphCyclicException.class )
	public void testDetectCyclicGraph() throws SecurityException, NoSuchMethodException, ClassNotFoundException, InitializationError,
	        GraphCyclicException {

		List<MyTestMethod> methods = new ArrayList<MyTestMethod>();
		methods.add( new MyTestMethod(this.getClass().getMethod( "rootMethod" ), testClass ) );
		methods.add( new MyTestMethod(this.getClass().getMethod( "middleCyclicMethod" ), testClass ) );
		methods.add( new MyTestMethod(this.getClass().getMethod( "bottomCyclicMethod" ), testClass ) );
		methods.add( new MyTestMethod(this.getClass().getMethod( "cyclicMethod" ), testClass ) );

		@SuppressWarnings( "unused" )
		DependencyGraph cyclicGraph = new DependencyGraph( methods, new MyTestClass( this.getClass() ) );
	}

	public void rootMethod() {

	}

	@Depends( "rootMethod" )
	public void middleMethod() {

	}

	@Depends( "middleMethod;rootMethod" )
	public void bottomMethod() {

	}

	@Depends( "bottomCyclicMethod" )
	public void cyclicMethod() {

	}

	@Depends( "rootMethod;cyclicMethod" )
	public void middleCyclicMethod() {

	}

	@Depends( "middleCyclicMethod;rootMethod" )
	public void bottomCyclicMethod() {

	}

}
