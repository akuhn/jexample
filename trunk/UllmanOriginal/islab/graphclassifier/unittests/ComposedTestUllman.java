package islab.graphclassifier.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import islab.graphclassifier.graph.algorithms.Ullman;
import islab.graphclassifier.graph.algorithms.Ullman.VertexPair;

import java.util.List;
import java.util.Set;

import org.junit.runner.RunWith;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import extension.ComposedTestRunner;
import extension.annotations.Depends;
import extension.annotations.DependsOnBefore;
import extension.annotations.MyTest;

@RunWith( ComposedTestRunner.class )
public class ComposedTestUllman {

	public ComposedTestUllman() {

	}

	@MyTest
	public DirectedSparseGraph testEmptyGraphIsIsomorphToItself() {
		DirectedSparseGraph g = new DirectedSparseGraph();

		assertTrue( Ullman.areIsomorph( g, g ) );
		return g;
	}

	@MyTest
	@DependsOnBefore
	public DirectedSparseGraph testEmptyGraphsAreSymmetricallyIsomorph( DirectedSparseGraph g ) {
		DirectedSparseGraph gi = new DirectedSparseGraph();

		assertTrue( Ullman.areIsomorph( g, gi ) );
		assertTrue( Ullman.areIsomorph( gi, g ) );

		return g;
	}

	@MyTest
	@DependsOnBefore
	public DirectedSparseGraph testAddVerticesNotIsomorph( DirectedSparseGraph g ) {
		Vertex v1 = g.addVertex( new DirectedSparseVertex() );
		Vertex v2 = g.addVertex( new DirectedSparseVertex() );
		DirectedSparseEdge edge = new DirectedSparseEdge( v1, v2 );
		g.addEdge( edge );

		assertFalse( Ullman.areIsomorph( g, new DirectedSparseGraph() ) );

		return g;
	}

	@MyTest
	@DependsOnBefore
	public void testRemoveVertexAreIsomorph( DirectedSparseGraph g ) {
		Set< DirectedSparseEdge> edges = g.getEdges();
		for ( DirectedSparseEdge edge : edges ) {
			g.removeEdge( edge );
			g.removeVertex( edge.getDest() );
			g.removeVertex( edge.getSource() );

			break;
		}

		assertTrue( Ullman.areIsomorph( g, new DirectedSparseGraph() ) );
	}

	@MyTest
	@Depends( "testAddVerticesNotIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph)" )
	public void testStillIsomorphWithItself( DirectedSparseGraph g ) {
		assertTrue( Ullman.areIsomorph( g, g ) );
	}

	@MyTest
	@Depends( "testAddVerticesNotIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph)" )
	public DirectedSparseGraph testSubgraphIsomorphismIsFound( DirectedSparseGraph g ) {
		DirectedSparseGraph gj = new DirectedSparseGraph();
		Vertex vX = gj.addVertex( new DirectedSparseVertex() );
		Vertex vY = gj.addVertex( new DirectedSparseVertex() );
		gj.addEdge( new DirectedSparseEdge( vX, vY ) );

		List< Set< VertexPair>> subgraphs = Ullman.subgraphIsomorphism( g, gj );
		assertEquals( 1, subgraphs.size() );

		return g;
	}

	@MyTest
	@DependsOnBefore
	public void testSameStructureIsIsomorph( DirectedSparseGraph g ) {
		// TODO: Oct 28, 2007,1:37:49 PM: this is ugly :-(
		DirectedSparseGraph gj = new DirectedSparseGraph();
		Vertex vX = gj.addVertex( new DirectedSparseVertex() );
		Vertex vY = gj.addVertex( new DirectedSparseVertex() );
		gj.addEdge( new DirectedSparseEdge( vX, vY ) );

		assertTrue( Ullman.areIsomorph( g, gj ) );
		assertTrue( Ullman.areIsomorph( gj, g ) );
	}

	@MyTest
	@Depends( "testSubgraphIsomorphismIsFound(edu.uci.ics.jung.graph.impl.DirectedSparseGraph)" )
	public void testNotIsomorphWithSelfLoop( DirectedSparseGraph g ) {
		// TODO: Oct 28, 2007,1:37:49 PM: this is ugly :-(
		DirectedSparseGraph gj = new DirectedSparseGraph();
		Vertex vX = gj.addVertex( new DirectedSparseVertex() );
		Vertex vY = gj.addVertex( new DirectedSparseVertex() );
		gj.addEdge( new DirectedSparseEdge( vX, vY ) );

		Set< Vertex> vertices = gj.getVertices();
		for ( Vertex vertex : vertices ) {
			gj.addEdge( new DirectedSparseEdge( vertex, vertex ) );
			break;
		}
		assertFalse( Ullman.areIsomorph( g, gj ) );
	}

}
