/**
 * 
 */
package extension.graph;

import java.util.ArrayList;
import java.util.List;

import extension.MyTestMethod;

/**
 * @author Lea Hänsenberger (lhaensenberger at students.unibe.ch)
 */
public class TestNode {

	private final MyTestMethod testMethod;

	private TestNode parentNode;

	private List<TestNode> childrenNodes;

	public TestNode( MyTestMethod testMethod ) {
		this.testMethod = testMethod;
		this.childrenNodes = new ArrayList<TestNode>();
	}

	public MyTestMethod getTestMethod() {
		assert this.testMethod != null;

		return this.testMethod;
	}

	public void setParent( TestNode node ) {
		this.parentNode = node;
	}

	public TestNode getParent() {
		assert this.parentNode != null;

		return this.parentNode;
	}

	public void addChild( TestNode node ) {
		assert this.childrenNodes != null;
		assert node != null;

		if ( !this.childrenNodes.contains( node ) ) {
			this.childrenNodes.add( node );
		}

		assert this.childrenNodes.size() > 0;
	}

	public List<TestNode> getChildren() {
		assert this.childrenNodes != null;

		return this.childrenNodes;
	}

}
