/**
 * 
 */
package extension.graph.tests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import extension.MyTestClass;
import extension.annotations.Depends;
import extension.graph.TestGraph;
import extension.graph.TestNode;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class TestGraphTest {

	private TestGraph graph;

	private List<Method> methods;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.methods = new ArrayList<Method>();
		this.methods.add( this.getClass().getMethod( "rootMethod" ) );
		this.methods.add( this.getClass().getMethod( "middleMethod" ) );
		this.methods.add( this.getClass().getMethod( "bottomMethod" ) );

		this.graph = new TestGraph( this.methods, new MyTestClass( this.getClass() ) );
	}

	@Test
	public void testCreateGraph() {
		assertEquals( 3, this.graph.getNodes().size() );
	}
	
	@Test
	public void testRootHasChildren() throws SecurityException, NoSuchMethodException{
		TestNode node = this.graph.getEqualNode(new TestNode(this.getClass().getMethod( "rootMethod" )));
		assertEquals( 2, node.getChildren().size() );
	}
	
	@Test
	public void testBottomHasNoChildren() throws SecurityException, NoSuchMethodException{		
		TestNode node = this.graph.getEqualNode(new TestNode(this.getClass().getMethod( "bottomMethod" )));
		assertEquals( 0, node.getChildren().size() );
	}
	
	@Test
	public void testRootHasNoParent() throws SecurityException, NoSuchMethodException{
		TestNode node = this.graph.getEqualNode(new TestNode(this.getClass().getMethod( "rootMethod" )));
		assertEquals( 0, node.getParents().size() );
	}
	
	@Test
	public void testBottomHasParents() throws SecurityException, NoSuchMethodException{
		TestNode node = this.graph.getEqualNode(new TestNode(this.getClass().getMethod( "bottomMethod" )));
		assertEquals( 2, node.getParents().size() );
	}
	
	@Test
	public void testMiddleHasParentsAndChildren() throws SecurityException, NoSuchMethodException{
		TestNode node = this.graph.getEqualNode(new TestNode(this.getClass().getMethod( "middleMethod" )));
		assertEquals( 1, node.getParents().size() );

		assertEquals( 1, node.getChildren().size() );
	}

	public void rootMethod() {

	}

	@Depends( "rootMethod" )
	public void middleMethod() {

	}
	
	@Depends( "middleMethod;rootMethod" )
	public void bottomMethod() {

	}

}
