package islab.graphclassifier.unittests.composable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import islab.graphclassifier.graph.algorithms.Ullman;

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
public class IsomorphTestUllman {

	public IsomorphTestUllman() {

	}

	@MyTest
	public DirectedSparseGraph testIsomorphWithItself() {
		DirectedSparseGraph gj = new DirectedSparseGraph();
		Vertex vX = gj.addVertex( new DirectedSparseVertex() );
		Vertex vY = gj.addVertex( new DirectedSparseVertex() );
		Vertex vZ = gj.addVertex( new DirectedSparseVertex() );
		gj.addEdge( new DirectedSparseEdge( vX, vY ) );
		gj.addEdge( new DirectedSparseEdge( vY, vZ ) );
		gj.addEdge( new DirectedSparseEdge( vZ, vX ) );

		assertTrue( Ullman.areIsomorph( gj, gj ) );

		return gj;
	}

	@MyTest
	@DependsAbove
	public DirectedSparseGraph testGraphWithSelfEdgeIsIsomorphToItself( DirectedSparseGraph gj ) {
		DirectedSparseGraph gi = ( DirectedSparseGraph ) gj.copy();

		for ( Object vertex : gi.getVertices() ) {
			gi.addEdge( new DirectedSparseEdge( ( Vertex ) vertex, ( Vertex ) vertex ) );
			break;
		}

		assertTrue( Ullman.areIsomorph( gi, gi ) );

		return gi;
	}

	@MyTest
	public DirectedSparseGraph testEmptyGraphIsIsomorphToItself() {
		DirectedSparseGraph g = new DirectedSparseGraph();

		assertTrue( Ullman.areIsomorph( g, g ) );
		return g;
	}

	@MyTest
	@DependsAbove
	public void testEmptyGraphsAreSymmetricallyIsomorph( DirectedSparseGraph g ) {
		DirectedSparseGraph gi = new DirectedSparseGraph();

		assertTrue( Ullman.areIsomorph( g, gi ) );
		assertTrue( Ullman.areIsomorph( gi, g ) );
	}

	@MyTest
	@Depends( "testEmptyGraphIsIsomorphToItself" )
	public DirectedSparseGraph testAddVertexNotIsomorph( DirectedSparseGraph g ) {
		DirectedSparseGraph gi = ( DirectedSparseGraph ) g.copy();
		gi.addVertex( new DirectedSparseVertex() );

		assertFalse( Ullman.areIsomorph( g, gi ) );
		assertFalse( Ullman.areIsomorph( gi, g ) );
		
		return gi;
	}
	
	@MyTest
	@Depends("testEmptyGraphIsIsomorphToItself;testAddVertexNotIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph)")
	public void testRemoveVertexIsomorphAgain(DirectedSparseGraph g, DirectedSparseGraph gi){
		gi.removeAllVertices();

		assertTrue( Ullman.areIsomorph( g, gi ) );
		assertTrue( Ullman.areIsomorph( gi, g ) );
	}

	@MyTest
	@Depends( "testEmptyGraphIsIsomorphToItself;testIsomorphWithItself" )
	public DirectedSparseGraph testSymmetricallyIsomorph( DirectedSparseGraph g, DirectedSparseGraph gj ) {
		g = ( DirectedSparseGraph ) g.copy();

		Vertex v1 = g.addVertex( new DirectedSparseVertex() );
		Vertex v2 = g.addVertex( new DirectedSparseVertex() );
		Vertex v3 = g.addVertex( new DirectedSparseVertex() );

		g.addEdge( new DirectedSparseEdge( v1, v2 ) );
		g.addEdge( new DirectedSparseEdge( v2, v3 ) );
		g.addEdge( new DirectedSparseEdge( v3, v1 ) );

		assertTrue( Ullman.areIsomorph( g, gj ) );
		assertTrue( Ullman.areIsomorph( gj, g ) );

		return g;
	}

	@MyTest
	@Depends( "testSymmetricallyIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph);"
			+ "testGraphWithSelfEdgeIsIsomorphToItself(edu.uci.ics.jung.graph.impl.DirectedSparseGraph)" )
	public void testNotIsomorphWithOneSelfEdge( DirectedSparseGraph g, DirectedSparseGraph gi ) {

		assertFalse( Ullman.areIsomorph( g, gi ) );
		assertFalse( Ullman.areIsomorph( gi, g ) );
	}

	@MyTest
	@Depends( "testSymmetricallyIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph);"
			+ "testGraphWithSelfEdgeIsIsomorphToItself(edu.uci.ics.jung.graph.impl.DirectedSparseGraph)" )
	public DirectedSparseGraph testNotIsomorphWithOneSelfEdgeAndSameDegree( DirectedSparseGraph g,
			DirectedSparseGraph gi ) {
		g = ( DirectedSparseGraph ) g.copy();

		assertFalse( g.getEdges().size() == gi.getEdges().size() );

		for ( Object edge : g.getEdges() ) {
			DirectedSparseEdge anEdge = ( DirectedSparseEdge ) edge;
			g.addEdge( new DirectedSparseEdge( anEdge.getDest(), anEdge.getSource() ) );
			break;
		}

		assertEquals( g.getEdges().size(), gi.getEdges().size() );

		assertFalse( Ullman.areIsomorph( g, gi ) );
		assertFalse( Ullman.areIsomorph( gi, g ) );

		return g;
	}

	@MyTest
	@Depends( "testSymmetricallyIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph);"
			+ "testGraphWithSelfEdgeIsIsomorphToItself(edu.uci.ics.jung.graph.impl.DirectedSparseGraph)" )
	public void testIsomorphWithBothSelfEdge( DirectedSparseGraph g, DirectedSparseGraph gi ) {
		g = ( DirectedSparseGraph ) g.copy();

		for ( Object vertex : g.getVertices() ) {
			g.addEdge( new DirectedSparseEdge( ( Vertex ) vertex, ( Vertex ) vertex ) );
			break;
		}

		assertTrue( Ullman.areIsomorph( g, gi ) );
		assertTrue( Ullman.areIsomorph( gi, g ) );
	}

	@MyTest
	@Depends( "testSymmetricallyIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph);"
			+ "testIsomorphWithItself" )
	public void testRewireEdge( DirectedSparseGraph g, DirectedSparseGraph gj ) {
		g = ( DirectedSparseGraph ) g.copy();
		
		Vertex source = null, oldDest = null, newDest = null;

		for(Object edge : g.getEdges()){
			if(source == null){
				source = ( ( DirectedSparseEdge ) edge ).getSource();
				oldDest = ( ( DirectedSparseEdge ) edge ).getDest();
			} else {
				newDest = ( ( DirectedSparseEdge ) edge ).getDest();
				break;
			}
		}
		
		g.removeEdge( source.findEdge( oldDest ) );
		g.addEdge( new DirectedSparseEdge(source, newDest) );
		
		assertFalse( Ullman.areIsomorph( g, gj ) );
		assertFalse( Ullman.areIsomorph( gj, g ) );
		
		g.removeEdge( source.findEdge( newDest ) );
		g.addEdge( new DirectedSparseEdge(source, oldDest) );
		
		assertTrue( Ullman.areIsomorph( g, gj ) );
		assertTrue( Ullman.areIsomorph( gj, g ) );
	}
	
	@MyTest
	@Depends( "testSymmetricallyIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph);"
			+ "testIsomorphWithItself" )
	public void testAddRemoveVertex( DirectedSparseGraph g, DirectedSparseGraph gj ) {
		g = ( DirectedSparseGraph ) g.copy();
		
		Vertex added = g.addVertex( new DirectedSparseVertex() );
		
		assertFalse( Ullman.areIsomorph( g, gj ) );
		assertFalse( Ullman.areIsomorph( gj, g ) );
		
		g.removeVertex( added );
		
		assertTrue( Ullman.areIsomorph( g, gj ) );
		assertTrue( Ullman.areIsomorph( gj, g ) );
	}
	
	@MyTest
	@Depends( "testSymmetricallyIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph);"
			+ "testIsomorphWithItself" )
	public void testAddRemoveVertexAndEdge( DirectedSparseGraph g, DirectedSparseGraph gj ) {
		g = ( DirectedSparseGraph ) g.copy();
		
		Vertex added = g.addVertex( new DirectedSparseVertex() );
		Vertex existing = ( Vertex ) g.getVertices().iterator().next();
		
		g.addEdge( new DirectedSparseEdge(existing, added) );
		
		assertFalse( Ullman.areIsomorph( g, gj ) );
		assertFalse( Ullman.areIsomorph( gj, g ) );
		
		g.removeVertex( added );
		
		assertTrue( Ullman.areIsomorph( g, gj ) );
		assertTrue( Ullman.areIsomorph( gj, g ) );
	}
	
	@MyTest
	@Depends( "testSymmetricallyIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph);"
			+ "testIsomorphWithItself" )
	public void testRemoveRestoreVertex( DirectedSparseGraph g, DirectedSparseGraph gj ) {
		g = ( DirectedSparseGraph ) g.copy();
		
		Vertex toRemove = ( Vertex ) g.getVertices().iterator().next();
		Set<DirectedSparseEdge> toRestore = toRemove.getIncidentEdges();
		
		g.removeVertex( toRemove );
		
		assertFalse( Ullman.areIsomorph( g, gj ) );
		assertFalse( Ullman.areIsomorph( gj, g ) );
		
		g.addVertex( toRemove );
		for ( DirectedSparseEdge directedSparseEdge : toRestore ) {
			g.addEdge( directedSparseEdge );
		}
		
		assertTrue( Ullman.areIsomorph( g, gj ) );
		assertTrue( Ullman.areIsomorph( gj, g ) );
	}
}
