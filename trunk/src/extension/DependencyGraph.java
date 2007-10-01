/**
 * 
 */
package extension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.InitializationError;

import extension.graph.exception.GraphCyclicException;
import extension.graph.exception.ParentExistsException;

/**
 * This class is the representation of a graph of tests. From a <code>List</code> of <code>Method</code> Objects
 * <code>TestNode</code> Objects are created. Then the nodes are sorted by a topoligical sort. The <code>TestGraph</code>
 * throws a <code>GraphCyclicException</code> if a cycle is detected during the topological sort.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class DependencyGraph {

	private List<MyTestMethod> nodes, sorted;

	private final MyTestClass testClass;

	private List<MyTestMethod> testMethods;

	public DependencyGraph( List<MyTestMethod> methods, MyTestClass testClass ) throws SecurityException, NoSuchMethodException, ClassNotFoundException,
	        GraphCyclicException, InitializationError {
		this.testClass = testClass;
		this.nodes = new ArrayList<MyTestMethod>();
		this.sorted = new ArrayList<MyTestMethod>();
		this.testMethods = methods;
		this.createNodes();
		this.sort();
	}

	/**
	 * @return a <code>List</code> of <code>TestNode</code> Objects
	 */
	public List<MyTestMethod> getNodes() {
		return this.nodes;
	}

	/**
	 * @return a <code>List</code> of topoligical sorted <code>TestNode</code> Objects
	 */
	public List<MyTestMethod> getSortedNodes() {
		return this.sorted;
	}

	/**
	 * Searches the <code>TestNode</code> which contains the same
	 * <code>MyTestMethod</code> as <code>node</code>.
	 * 
	 * @param node
	 * @return a <code>TestNode</code>
	 */
	public MyTestMethod getEqualNode( MyTestMethod node ) {
		for ( MyTestMethod testNode : this.nodes ) {
			if ( node.equals( testNode ) ) {
				return testNode;
			}
		}
		return null;
	}

	private void sort() throws GraphCyclicException, InitializationError {
		List<MyTestMethod> origNodes = new ArrayList<MyTestMethod>();
		origNodes.addAll( this.nodes );

		while ( !origNodes.isEmpty() ) {
			List<MyTestMethod> withoutParents = this.getNodesWithoutParents( origNodes );
			if ( !this.deleteFromNodes( origNodes, withoutParents ) || !this.addNodesToSorted( withoutParents ) ) {
				throw new InitializationError( "Could not delete or add nodes." );
			}
		}
	}

	private boolean addNodesToSorted( List<MyTestMethod> withoutParents ) {
		assert withoutParents != null;

		return this.sorted.addAll( withoutParents );
	}

	private boolean deleteFromNodes( List<MyTestMethod> origNodes, List<MyTestMethod> withoutParents ) {
		assert withoutParents != null;

		return origNodes.removeAll( withoutParents );
	}

	private List<MyTestMethod> getNodesWithoutParents( List<MyTestMethod> origNodes ) throws GraphCyclicException {
		List<MyTestMethod> withoutParents = new ArrayList<MyTestMethod>();

		for ( MyTestMethod testNode : origNodes ) {
			if ( !this.hasParentsInOrigNodes( testNode, origNodes ) ) {
				withoutParents.add( testNode );
			}
		}

		if ( withoutParents.isEmpty() ) {
			throw new GraphCyclicException( "The dependencies are cyclic." );
		}

		return withoutParents;
	}

	private boolean hasParentsInOrigNodes( MyTestMethod testNode, List<MyTestMethod> origNodes ) {

		for ( MyTestMethod node : testNode.getParents() ) {
			if ( origNodes.contains( node ) ) {
				return true;
			}
		}

		return false;
	}

	private void createNodes() throws SecurityException, NoSuchMethodException, ClassNotFoundException, GraphCyclicException, InitializationError {
		assert !this.testMethods.isEmpty();

		for ( MyTestMethod method : this.testMethods ) {
			this.addNode( method );
		}
	}

	private void addNode( MyTestMethod node ) throws SecurityException, NoSuchMethodException, ClassNotFoundException, GraphCyclicException, InitializationError {

		// if the child is null, then it's the node we start to build the graph with

		if ( !this.nodes.contains( node ) ) {
			for ( Method dependency : node.getTestMethod().getDependencies() ) {
				try {
					node.addParent( this.getCorrespondingTestMethod( dependency ) );
				} catch ( ParentExistsException e ) {
					throw new GraphCyclicException( "The dependencies for this Test contains cycles." );
				}
			}
			this.nodes.add( node );
		}
	}

	private MyTestMethod getCorrespondingTestMethod( Method dependency ) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InitializationError {
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
