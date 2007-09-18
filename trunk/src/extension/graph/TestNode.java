/**
 * 
 */
package extension.graph;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class TestNode {

	private final Method testMethod;

	private List<TestNode> parentNodes;

	private List<TestNode> childrenNodes;

	public TestNode( Method testMethod ) {
		this.testMethod = testMethod;
		this.childrenNodes = new ArrayList<TestNode>();
		this.parentNodes = new ArrayList<TestNode>();
	}

	/**
	 * This constructor is only used to initialize the
	 * start node of the <code>TestGraph</code>
	 */
	// public TestNode() {
	// this.testMethod = null;
	//		
	// this.parentNodes = null;
	// this.childrenNodes = new ArrayList<TestNode>();
	// }
	public Method getTestMethod() {
		assert this.testMethod != null;

		return this.testMethod;
	}

	public void addParent( TestNode node ) {
		assert node != null;
		assert this.parentNodes != null;

		if ( !this.parentNodes.contains( node ) && !this.equals( node ) ) {
			this.parentNodes.add( node );
		}
	}

	public List<TestNode> getParents() {
		return this.parentNodes;
	}

	public void addChild( TestNode node ) {
		assert this.childrenNodes != null;
		assert node != null;

		if ( !this.childrenNodes.contains( node ) && !this.equals( node ) ) {
			this.childrenNodes.add( node );
		}
	}

	public List<TestNode> getChildren() {
		assert this.childrenNodes != null;

		return this.childrenNodes;
	}

	public boolean equals( Object obj ) {
		return this.getTestMethod().equals( ( (TestNode) obj ).getTestMethod() );
	}

}
