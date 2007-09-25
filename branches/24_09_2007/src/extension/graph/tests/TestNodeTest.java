/**
 * 
 */
package extension.graph.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import extension.graph.TestNode;
import extension.graph.exception.ParentExistsException;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class TestNodeTest {

	private Method testMethod, testMethod2;

	private TestNode node, node2;

	private TestNode node4;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.testMethod = this.getClass().getMethod( "methodToAdd" );
		this.testMethod2 = this.getClass().getMethod( "methodToAdd2" );

		this.node = new TestNode( this.testMethod );
		this.node2 = new TestNode( this.testMethod2 );
		this.node4 = new TestNode( this.testMethod );
	}

	@Test
	public void testGetTestMethod() {
		assertEquals( this.testMethod, this.node.getTestMethod() );

		assertFalse( this.testMethod2.equals( this.node.getTestMethod() ) );
	}

	@Test
	public void testSetAndGetParent() throws ParentExistsException {
		this.node2.addParent( this.node );
		assertEquals( 1, this.node2.getParents().size() );
	}

	@Test(expected = ParentExistsException.class)
	public void testAddParentOnlyOnce() throws ParentExistsException {
		this.node2.addParent( this.node );
		assertEquals( 1, this.node2.getParents().size() );

		this.node2.addParent( this.node );
		assertFalse( 2 == this.node2.getParents().size() );
		assertEquals( 1, this.node2.getParents().size() );
	}

	@Test(expected = ParentExistsException.class)
	public void testDontAddMeAsMyParent() throws ParentExistsException {
		this.node.addParent( this.node4 );
		assertEquals( 0, this.node.getParents().size() );
	}


	public void methodToAdd() {

	}

	public void methodToAdd2() {

	}

	public void methodToAdd3() {

	}

}
