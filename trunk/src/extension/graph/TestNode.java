/**
 * 
 */
package extension.graph;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import extension.graph.exception.ParentExistsException;

/**
 * This class represents the node of the <code>TestGraph</code>. It knows it's testmethod
 * and it's parents.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class TestNode {

	private final Method testMethod;

	private List<TestNode> parentNodes;
	
	//TODO: this object should know, if it's testmethod failed or is skipped
	//becaus it's parent has failed or was skipped.

	public TestNode( Method testMethod ) {
		this.testMethod = testMethod;
		this.parentNodes = new ArrayList<TestNode>();
	}

	
	/**
	 * @return the represented <code>Method</code>
	 */
	public Method getTestMethod() {
		assert this.testMethod != null;

		return this.testMethod;
	}

	/**
	 * @param node the <code>TestNode</code> to be added to the parents list
	 * @throws ParentExistsException this <code>Exception</code> is thrown if a <code>TestNode</code>
	 * that already is a parent of this <code>TestNode</code> should be added. => cycle detection
	 */
	public void addParent( TestNode node ) throws ParentExistsException {
		assert node != null;
		assert this.parentNodes != null;

		if ( !this.parentNodes.contains( node ) && !this.equals( node ) ) {
			this.parentNodes.add( node );
		} else {
			throw new ParentExistsException();
		}
	}

	/**
	 * @return the <code>List</code> of parents
	 */
	public List<TestNode> getParents() {
		return this.parentNodes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( Object obj ) {
		return this.getTestMethod().equals( ( (TestNode) obj ).getTestMethod() );
	}

}
