/**
 * 
 */
package extension.graph;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import extension.MyTestClass;
import extension.annotations.Depends;
import extension.parser.DependencyParser;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class TestGraph {

	private List<TestNode> nodes;

	private final MyTestClass testClass;

	public TestGraph( List<Method> methods, MyTestClass testClass ) throws SecurityException, NoSuchMethodException, ClassNotFoundException {
		this.testClass = testClass;
		this.nodes = new ArrayList<TestNode>();

		this.createNodes( methods );
	}

	public List<TestNode> getNodes() {
		return this.nodes;
	}

	private void createNodes( List<Method> methods ) throws SecurityException, NoSuchMethodException, ClassNotFoundException {
		assert !methods.isEmpty();

		TestNode node;
		for ( Method method : methods ) {
			node = new TestNode( method );
			node.addParent( this.addNode( node, null ) );
		}
	}

	private TestNode addNode( TestNode node, TestNode child ) throws SecurityException, NoSuchMethodException, ClassNotFoundException {

		// if the child is null, then it's the node we start to build the graph with

		if ( !this.nodes.contains( node ) ) {
			Depends annotation = node.getTestMethod().getAnnotation( Depends.class );
			if ( annotation != null ) {
				List<Method> deps = new DependencyParser( annotation.value(), this.testClass ).getDependencies();
				for ( Method dependency : deps ) {
					node.addParent( this.addNode( new TestNode( dependency ), node ) );
				}
			}
			this.nodes.add( node );
		} else {
			node = this.getEqualNode( node );
		}

		if ( child != null ) {
			node.addChild( child );
		}
		return node;
	}

	public TestNode getEqualNode( TestNode node ) {
		for ( TestNode testNode : this.nodes ) {
			if ( node.equals( testNode ) ) {
				return testNode;
			}
		}
		return null;
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
