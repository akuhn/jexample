package islab.graphclassifier.unittests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import islab.graphclassifier.graph.algorithms.Ullman;

import org.junit.runner.RunWith;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import extension.ComposedTestRunner;
import extension.annotations.DependsOnBefore;
import extension.annotations.MyTest;

@RunWith( ComposedTestRunner.class )
public class ComposedTestUllman {

	private DirectedSparseGraph g;

	public ComposedTestUllman() {

	}

	@MyTest
	public void testEmptyGraphIsIsomorphToItself() {
		this.g = new DirectedSparseGraph();

		assertTrue( Ullman.areIsomorph( this.g, this.g ) );
	}

	@MyTest
	@DependsOnBefore
	public void testEmptyGraphsAreSymmetricallyIsomorph() {
		DirectedSparseGraph gi = new DirectedSparseGraph();

		assertTrue( Ullman.areIsomorph( this.g, gi ) );
		assertTrue( Ullman.areIsomorph( gi, this.g ) );
	}

	@MyTest
	@DependsOnBefore
	public DirectedSparseEdge testAddVerticesNotIsomorph() {
		Vertex v1 = g.addVertex( new DirectedSparseVertex() );
		Vertex v2 = g.addVertex( new DirectedSparseVertex() );
		DirectedSparseEdge edge = new DirectedSparseEdge( v1, v2 );
		g.addEdge(edge);

		assertFalse( Ullman.areIsomorph( this.g, new DirectedSparseGraph() ) );

		return edge;
	}

	@MyTest
	@DependsOnBefore
	public void testRemoveVertexAreIsomorph( DirectedSparseEdge edge ) {
		this.g.removeEdge( edge );
		this.g.removeVertex( edge.getDest() );
		this.g.removeVertex( edge.getSource() );
		
		assertTrue( Ullman.areIsomorph( this.g, new DirectedSparseGraph() ) );
	}

}
