package islab.graphclassifier.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import islab.graphclassifier.graph.algorithms.Ullman;
import islab.graphclassifier.graph.algorithms.Ullman.VertexPair;

import java.util.List;
import java.util.Set;

import org.junit.runner.RunWith;

import edu.uci.ics.jung.graph.Edge;
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
	public DirectedSparseGraph testIsomorphWithItself() {
		DirectedSparseGraph gj = new DirectedSparseGraph();
		Vertex vX = gj.addVertex( new DirectedSparseVertex() );
		Vertex vY = gj.addVertex( new DirectedSparseVertex() );
		gj.addEdge( new DirectedSparseEdge( vX, vY ) );

		assertTrue( Ullman.areIsomorph( gj, gj ) );

		return gj;
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
		g = ( DirectedSparseGraph ) g.copy();
		DirectedSparseGraph gi = new DirectedSparseGraph();

		assertTrue( Ullman.areIsomorph( g, gi ) );
		assertTrue( Ullman.areIsomorph( gi, g ) );

		return g;
	}

	@MyTest
	@DependsOnBefore
	public DirectedSparseGraph testAddVerticesNotIsomorph( DirectedSparseGraph g ) {
		g = ( DirectedSparseGraph ) g.copy();
		//fails, because cloning is not implemented correctly
		Vertex v1 = g.addVertex( new DirectedSparseVertex() );
		Vertex v2 = g.addVertex( new DirectedSparseVertex() );
		g.addEdge( new DirectedSparseEdge( v1, v2 ) );

		assertFalse( Ullman.areIsomorph( g, new DirectedSparseGraph() ) );

		return g;
	}

	@MyTest
	@DependsOnBefore
	public void testRemoveVertexAreIsomorph( DirectedSparseGraph g ) {
		g = ( DirectedSparseGraph ) g.copy();
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
	@Depends( "testAddVerticesNotIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph);testIsomorphWithItself" )
	public DirectedSparseGraph testSubgraphIsomorphismIsFound( DirectedSparseGraph g, DirectedSparseGraph gj ) {
		List< Set< VertexPair>> subgraphs = Ullman.subgraphIsomorphism( g, gj );
		assertEquals( 1, subgraphs.size() );

		return g;
	}

	@MyTest
	@Depends( "testAddVerticesNotIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph);testIsomorphWithItself" )
	public DirectedSparseGraph testSameStructureIsIsomorph( DirectedSparseGraph g, DirectedSparseGraph gj ) {
		assertTrue( Ullman.areIsomorph( g, gj ) );
		assertTrue( Ullman.areIsomorph( gj, g ) );

		return gj;
	}

	@MyTest
	@Depends( "testAddVerticesNotIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph);"
			+ "testSameStructureIsIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph)" )
	public DirectedSparseGraph testNotIsomorphWithSelfLoop( DirectedSparseGraph g, DirectedSparseGraph gj ) {
		gj = ( DirectedSparseGraph ) gj.copy();
		//fails, because cloning is not implemented correctly
		Set< Vertex> vertices = gj.getVertices();
		for ( Vertex vertex : vertices ) {
			gj.addEdge( new DirectedSparseEdge( vertex, vertex ) );
			break;
		}
		assertFalse( Ullman.areIsomorph( g, gj ) );
		return g;
	}

	@MyTest
	@Depends( "testAddVerticesNotIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph);"
			+ "testNotIsomorphWithSelfLoop(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph)" )
	public void testSubgraphFoundWithSelfLoop( DirectedSparseGraph g, DirectedSparseGraph gj ) {
		List< Set< VertexPair>> subgraphs = Ullman.subgraphIsomorphism( g, gj );
		assertEquals( 1, subgraphs.size() );
		subgraphs = Ullman.subgraphIsomorphism( gj, g );
		assertEquals( 1, subgraphs.size() );
	}

	@MyTest
	@Depends( "testAddVerticesNotIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph);" +
			"testSameStructureIsIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph)" )
	public DirectedSparseGraph testSubgraphIsomorphismChainCircle( DirectedSparseGraph g, DirectedSparseGraph gj ) throws CloneNotSupportedException {
		g = ( DirectedSparseGraph ) g.copy();
		gj = ( DirectedSparseGraph ) gj.copy();
		//fails, because cloning is not implemented correctly
		Vertex v3 = g.addVertex( new DirectedSparseVertex() );
		Vertex v4 = g.addVertex( new DirectedSparseVertex() );
		Vertex v1 = null, v2 = null;
		for ( Object edge : g.getEdges() ) {
			v1 = ( ( DirectedSparseEdge ) edge ).getSource();
			v2 = ( ( DirectedSparseEdge ) edge ).getDest();
		}

		g.addEdge( new DirectedSparseEdge( v2, v3 ) );
		g.addEdge( new DirectedSparseEdge( v3, v4 ) );
		g.addEdge( new DirectedSparseEdge( v4, v1 ) );
		
		Vertex vZ = gj.addVertex(new DirectedSparseVertex());
		for ( Object edge : gj.getEdges() ) {
			gj.addEdge(new DirectedSparseEdge(( ( DirectedSparseEdge ) edge ).getDest(), vZ));
		}
		
		List< Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, g);
	    assertEquals(4, subgraphs.size());
	    
	    subgraphs = Ullman.subgraphIsomorphism(g, gj);
	    assertEquals(0, subgraphs.size());

		return g;
	}
	
	@MyTest(expected = AssertionError.class)
	@Depends( "testAddVerticesNotIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph);" +
			"testSameStructureIsIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph)" )
	public void testModelLessEqualVertices(DirectedSparseGraph g, DirectedSparseGraph gj){
		Ullman.subgraphIsomorphism(g, gj);
	}
	
	@MyTest
	@Depends( "testSubgraphIsomorphismChainCircle(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph);" +
			"testSameStructureIsIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph)" )
	public DirectedSparseGraph testSubgraphIsomorphismChainSingleEdge( DirectedSparseGraph g, DirectedSparseGraph gj ) throws CloneNotSupportedException {

		List< Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, g);
	    assertEquals(4, subgraphs.size());
	    
	    subgraphs = Ullman.subgraphIsomorphism(g, gj);
	    assertEquals(0, subgraphs.size());

		return g;
	}
	
	@MyTest
	@Depends( "testSubgraphIsomorphismChainCircle(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph);" +
			"testSameStructureIsIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph)" )
	public DirectedSparseGraph testSubgraphIsomorphismChainSingleVertex( DirectedSparseGraph g, DirectedSparseGraph gj ) throws CloneNotSupportedException {
		gj = (DirectedSparseGraph) gj.copy();
		for (Object edge : gj.getEdges()) {
			Vertex v = ((DirectedSparseEdge) edge).getDest();
			gj.removeEdge((Edge) edge);
			gj.removeVertex(v);
		}
		
		assertEquals(1, gj.getVertices().size());
		
		List< Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, g);
	    assertEquals(4, subgraphs.size());
	    
	    subgraphs = Ullman.subgraphIsomorphism(g, gj);
	    assertEquals(0, subgraphs.size());

		return gj;
	}
	
	@MyTest
	@Depends( "testSubgraphIsomorphismChainCircle(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph);" +
			"testSubgraphIsomorphismChainSingleVertex(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph)" )
	public DirectedSparseGraph testSubgraphIsomorphismChainSingleVertexSelfLoop( DirectedSparseGraph g, DirectedSparseGraph gj ) throws CloneNotSupportedException {
		gj = (DirectedSparseGraph) gj.copy();
		for (Object vertex : gj.getVertices()) {
			gj.addEdge(new DirectedSparseEdge((Vertex)vertex,(Vertex)vertex));
		}
		
		List< Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, g);
	    assertEquals(0, subgraphs.size());
	    
	    subgraphs = Ullman.subgraphIsomorphism(g, gj);
	    assertEquals(0, subgraphs.size());

		return gj;
	}
}
