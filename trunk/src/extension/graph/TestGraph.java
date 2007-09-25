/**
 * 
 */
package extension.graph;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.InitializationError;

import extension.MyTestClass;
import extension.MyTestMethod;
import extension.graph.exception.GraphCyclicException;
import extension.graph.exception.ParentExistsException;

/**
 * This class is the representation of a graph of tests. From a <code>List</code> of <code>Method</code> Objects
 * <code>TestNode</code> Objects are created. Then the nodes are sorted by a topoligical sort. The <code>TestGraph</code>
 * throws a <code>GraphCyclicException</code> if a cycle is detected during the topological sort.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class TestGraph {

	private List<TestNode> nodes, sorted;

	private final MyTestClass testClass;

	private List<MyTestMethod> testMethods;

	public TestGraph( List<MyTestMethod> methods, MyTestClass testClass ) throws SecurityException, NoSuchMethodException, ClassNotFoundException,
	        GraphCyclicException, InitializationError {
		this.testClass = testClass;
		this.nodes = new ArrayList<TestNode>();
		this.sorted = new ArrayList<TestNode>();
		this.testMethods = methods;
		this.createNodes();
		this.sort();
	}

	/**
	 * @return a <code>List</code> of <code>TestNode</code> Objects
	 */
	public List<TestNode> getNodes() {
		return this.nodes;
	}

	/**
	 * @return a <code>List</code> of topoligical sorted <code>TestNode</code> Objects
	 */
	public List<TestNode> getSortedNodes() {
		return this.sorted;
	}

	/**
	 * Searches the <code>TestNode</code> which contains the same
	 * Method as <code>node</code>.
	 * 
	 * @param node
	 * @return a <code>TestNode</code>
	 */
	public TestNode getEqualNode( TestNode node ) {
		for ( TestNode testNode : this.nodes ) {
			if ( node.equals( testNode ) ) {
				return testNode;
			}
		}
		return null;
	}

	private void sort() throws GraphCyclicException, InitializationError {
		List<TestNode> origNodes = new ArrayList<TestNode>();
		origNodes.addAll( this.nodes );

		while ( !origNodes.isEmpty() ) {
			List<TestNode> withoutParents = this.getNodesWithoutParents( origNodes );
			if ( !this.deleteFromNodes( origNodes, withoutParents ) || !this.addNodesToSorted( withoutParents ) ) {
				throw new InitializationError( "Could not delete or add nodes." );
			}
		}
	}

	private boolean addNodesToSorted( List<TestNode> withoutParents ) {
		assert withoutParents != null;

		return this.sorted.addAll( withoutParents );
	}

	private boolean deleteFromNodes( List<TestNode> origNodes, List<TestNode> withoutParents ) {
		assert withoutParents != null;

		return origNodes.removeAll( withoutParents );
	}

	private List<TestNode> getNodesWithoutParents( List<TestNode> origNodes ) throws GraphCyclicException {
		List<TestNode> withoutParents = new ArrayList<TestNode>();

		for ( TestNode testNode : origNodes ) {
			if ( !this.hasParentsInOrigNodes( testNode, origNodes ) ) {
				withoutParents.add( testNode );
			}
		}

		if ( withoutParents.isEmpty() ) {
			throw new GraphCyclicException( "The dependencies are cyclic." );
		}

		return withoutParents;
	}

	private boolean hasParentsInOrigNodes( TestNode testNode, List<TestNode> origNodes ) {

		for ( TestNode node : testNode.getParents() ) {
			if ( origNodes.contains( node ) ) {
				return true;
			}
		}

		return false;
	}

	private void createNodes() throws SecurityException, NoSuchMethodException, ClassNotFoundException, GraphCyclicException {
		assert !this.testMethods.isEmpty();

		TestNode node;
		for ( MyTestMethod method : this.testMethods ) {
			node = new TestNode( method );
			this.addNode( node );
		}
	}

	private void addNode( TestNode node ) throws SecurityException, NoSuchMethodException, ClassNotFoundException, GraphCyclicException {

		// if the child is null, then it's the node we start to build the graph with

		if ( !this.nodes.contains( node ) ) {
			for ( Method dependency : node.getTestMethod().getDependencies() ) {
				try {
					node.addParent( new TestNode( this.getCorrespondingTestMethod( dependency ) ) );
				} catch ( ParentExistsException e ) {
					throw new GraphCyclicException( "The dependencies for this Test contains cycles." );
				}
			}
			this.nodes.add( node );
		}
	}

	private MyTestMethod getCorrespondingTestMethod( Method dependency ) throws SecurityException, ClassNotFoundException, NoSuchMethodException {
		for ( MyTestMethod method : this.testMethods ) {
			if ( method.getMethod().equals( dependency ) ) {
				return method;
			}
		}
		return new MyTestMethod( dependency, this.testClass );
	}

	// private boolean nodeExists( TestNode node ) {
	// MyTestMethod testMethod = node.getTestMethod();
	// for ( TestNode testNode : this.nodes ) {
	// if(testMethod.equals( testNode.getTestMethod() )){
	// return true;
	// }
	// }
	// return false;
	// }

	// public void addNode( TestNode node ) {
	// if ( !this.nodes.contains( node ) ) {
	// this.nodes.add( node );
	// }
	// }
}
