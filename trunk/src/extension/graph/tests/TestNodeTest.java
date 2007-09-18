/**
 * 
 */
package extension.graph.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import extension.MyTestClass;
import extension.MyTestMethod;
import extension.graph.TestNode;

/**
 * @author Lea Hänsenberger (lhaensenberger at students.unibe.ch)
 */
public class TestNodeTest {

	private MyTestMethod testMethod;

	private TestNode node;

	private MyTestMethod testMethod2;

	private TestNode node2;

	private TestNode node3;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testMethod = new MyTestMethod( this.getClass().getMethod( "methodToAdd" ), new MyTestClass( this.getClass() ) );
		testMethod2 = new MyTestMethod( this.getClass().getMethod( "methodToAdd" ), new MyTestClass( this.getClass() ) );

		node = new TestNode( testMethod );
		node2 = new TestNode( testMethod2 );
		node3 = new TestNode( testMethod );
	}

	@Test
	public void testGetTestMethod() {
		assertEquals( testMethod, node.getTestMethod() );

		assertFalse( this.testMethod2.equals( this.node.getTestMethod() ) );
	}

	@Test
	public void testSetAndGetParent() {
		this.node2.setParent( this.node );
		assertEquals( this.node, this.node2.getParent() );
		assertFalse( this.node2.equals( this.node2.getParent() ) );
	}

	@Test
	public void testAddAndGetChlidren() {
		this.node.addChild( this.node2 );
		assertEquals( 1, this.node.getChildren().size() );

		this.node.addChild( this.node3 );
		assertEquals( 2, this.node.getChildren().size() );
	}

	@Test
	public void testAddChildOnlyOnce() {
		this.node.addChild( this.node2 );
		assertEquals( 1, this.node.getChildren().size() );

		this.node.addChild( this.node2 );
		assertFalse( 2 == this.node.getChildren().size() );
	}

	public void methodToAdd() {

	}

}
