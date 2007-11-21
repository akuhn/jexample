package islab.graphclassifier.unittests.composable;

import static org.junit.Assert.assertEquals;
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
import extension.annotations.DependsAbove;
import extension.annotations.MyTest;

@RunWith( ComposedTestRunner.class )
public class SimpleSubgraphTest {
	public SimpleSubgraphTest() {
	}

	@MyTest
	public DirectedSparseGraph testSingleEdgeSubgraphIsomorphism() {
		DirectedSparseGraph gj = new DirectedSparseGraph();

		Vertex v1 = gj.addVertex( new DirectedSparseVertex() );
		Vertex v2 = gj.addVertex( new DirectedSparseVertex() );

		gj.addEdge( new DirectedSparseEdge( v1, v2 ) );

		DirectedSparseGraph g = ( DirectedSparseGraph ) gj.copy();

		List< Set< VertexPair>> subgraphs = Ullman.subgraphIsomorphism( gj, g );
		assertEquals( 1, subgraphs.size() );
		subgraphs = Ullman.subgraphIsomorphism( g, gj );
		assertEquals( 1, subgraphs.size() );

		return g;
	}

	@MyTest
	@DependsAbove
	public DirectedSparseGraph testInputMoreVertices( DirectedSparseGraph g ) {
		g = ( DirectedSparseGraph ) g.copy();
		DirectedSparseGraph gj = ( DirectedSparseGraph ) g.copy();

		g.addVertex( new DirectedSparseVertex() );

		List< Set< VertexPair>> subgraphs = Ullman.subgraphIsomorphism( gj, g );
		assertEquals( 1, subgraphs.size() );

		return g;
	}
	
	@MyTest(expected=AssertionError.class)
	@Depends("testInputMoreVertices(edu.uci.ics.jung.graph.impl.DirectedSparseGraph);testSingleEdgeSubgraphIsomorphism")
	public void testModelMoreVertices(DirectedSparseGraph g, DirectedSparseGraph gj){
		Ullman.subgraphIsomorphism( g, gj );
	}
}
