package islab.graphclassifier.unittests.original;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import islab.graphclassifier.graph.algorithms.Ullman;
import islab.graphclassifier.graph.algorithms.Ullman.VertexPair;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

public class TestUllman {
	private Graph g;
	private Graph gi;
	private Graph gj;

	@Before
	public void setUp() {
		g = new DirectedSparseGraph();
		gi = new DirectedSparseGraph();
		gj = new DirectedSparseGraph();
	}

	/*
	 * Test method for
	 * 'islab.graphclassifier.algorithms.graph.Ullman.areIsomorph(Graph, Graph)'
	 */
	@Test
	public void testAreIsomorph() {
		// reflexive, symmetric, transitive, consistent
		// for empty graphs

		// reflexive
		// begin IsomorphTestUllman.testEmptyGraphIsIsomorphToItself
		assertTrue(Ullman.areIsomorph(g, g));
		assertTrue(Ullman.areIsomorph(gi, gi));
		// end IsomorphTestUllman.testEmptyGraphIsIsomorphToItself

		// symmetric
		// begin IsomorphTestUllman.testEmptyGraphsAreSymmetricallyIsomorph
		assertTrue(Ullman.areIsomorph(g, gi));
		assertTrue(Ullman.areIsomorph(gi, g));
		// end IsomorphTestUllman.testEmptyGraphsAreSymmetricallyIsomorph

		// begin IsomorphTestUllman.testAddVertexNotIsomorph
		Vertex v1 = g.addVertex(new DirectedSparseVertex());

		assertFalse(Ullman.areIsomorph(g, gi));
		assertFalse(Ullman.areIsomorph(gi, g));
		// begin IsomorphTestUllman.testAddVertexNotIsomorph

		// begin IsomorphTestUllman.testIsomorphWithItself
		Vertex v2 = g.addVertex(new DirectedSparseVertex());
		Vertex v3 = g.addVertex(new DirectedSparseVertex());

		g.addEdge(new DirectedSparseEdge(v1, v2));
		g.addEdge(new DirectedSparseEdge(v2, v3));
		g.addEdge(new DirectedSparseEdge(v3, v1));

		// reflexive
		assertTrue(Ullman.areIsomorph(g, g));
		// end IsomorphTestUllman.testIsomorphWithItself

		// begin IsomorphTestUllman.testIsomorphWithItself
		Vertex vA = gi.addVertex(new DirectedSparseVertex());
		Vertex vB = gi.addVertex(new DirectedSparseVertex());
		Vertex vC = gi.addVertex(new DirectedSparseVertex());
		gi.addEdge(new DirectedSparseEdge(vA, vB));
		gi.addEdge(new DirectedSparseEdge(vB, vC));
		gi.addEdge(new DirectedSparseEdge(vC, vA));

		// reflexive
		assertTrue(Ullman.areIsomorph(gi, gi));
		// end IsomorphTestUllman.testIsomorphWithItself

		// symmetric
		// begin IsomorphTestUllman.testSymmetricallyIsomorph
		assertTrue(Ullman.areIsomorph(g, gi));
		assertTrue(Ullman.areIsomorph(gi, g));
		// end IsomorphTestUllman.testSymmetricallyIsomorph

		// begin IsomorphTestUllman.testSymmetricallyIsomorph (I think, that it's enough to show, that two graphs with the same vertices and edges are isomorph)
		Vertex vX = gj.addVertex(new DirectedSparseVertex());
		Vertex vY = gj.addVertex(new DirectedSparseVertex());
		Vertex vZ = gj.addVertex(new DirectedSparseVertex());
		gj.addEdge(new DirectedSparseEdge(vX, vY));
		gj.addEdge(new DirectedSparseEdge(vY, vZ));
		gj.addEdge(new DirectedSparseEdge(vZ, vX));

		// transitive
		assertTrue(Ullman.areIsomorph(g, gi));
		assertTrue(Ullman.areIsomorph(gi, gj));
		assertTrue(Ullman.areIsomorph(gj, g));
		// end IsomorphTestUllman.testSymmetricallyIsomorph
		
		// self-edges
		g.addEdge(new DirectedSparseEdge(v1, v1));
		gi.addEdge(new DirectedSparseEdge(vC, vC));
		
		// begin IsomorphTestUllman.testIsomorphWithBothSelfEdge
		assertTrue(Ullman.areIsomorph(g, gi));
		assertTrue(Ullman.areIsomorph(gi, g));
		// end IsomorphTestUllman.testIsomorphWithBothSelfEdge
		
		// begin IsomorphTestUllman.testNotIsomorphWithOneSelfEdge
		assertFalse(Ullman.areIsomorph(g, gj));
		assertFalse(Ullman.areIsomorph(gj, g));
		// end IsomorphTestUllman.testNotIsomorphWithOneSelfEdge
		
		// add an edge between nodes --> degree() will become the same
		
		// begin IsomorphTestUllman.testNotIsomorphWithOneSelfEdgeAndSameDegree
		g.addEdge(new DirectedSparseEdge(v2, v2));
		gj.addEdge(new DirectedSparseEdge(vY, vX));
		// should still fail
		assertFalse(Ullman.areIsomorph(g, gj));
		assertFalse(Ullman.areIsomorph(gj, g));
		// end IsomorphTestUllman.testNotIsomorphWithOneSelfEdgeAndSameDegree
		
		// restore
		// begin testRemoveSelfEdgesIsomorphAgain
		g.removeEdge(v1.findEdge(v1));
		g.removeEdge(v2.findEdge(v2));
		gi.removeEdge(vC.findEdge(vC));
		gj.removeEdge(vY.findEdge(vX));
		assertTrue(Ullman.areIsomorph(g, gi));
		assertTrue(Ullman.areIsomorph(gi, gj));
		assertTrue(Ullman.areIsomorph(gj, g));
		// end testRemoveSelfEdgesIsomorphAgain

		// add Edge
		// begin testAddRemoveEdge
		gj.addEdge(new DirectedSparseEdge(vX, vZ));
		assertFalse(Ullman.areIsomorph(g, gj));
		assertFalse(Ullman.areIsomorph(gj, g));
		// restore
		gj.removeEdge(vX.findEdge(vZ));
		assertTrue(Ullman.areIsomorph(g, gj));
		assertTrue(Ullman.areIsomorph(gj, g));
		// end testAddRemoveEdge

		// remove Edge
		// begin testRemoveRestoreEdge
		gj.removeEdge(vY.findEdge(vZ));
		assertFalse(Ullman.areIsomorph(g, gj));
		assertFalse(Ullman.areIsomorph(gj, g));
		// restore
		gj.addEdge(new DirectedSparseEdge(vY, vZ));
		assertTrue(Ullman.areIsomorph(g, gj));
		assertTrue(Ullman.areIsomorph(gj, g));
		// end testRemoveRestoreEdge

		// rewire Edge
		// begin IsomorphTestUllman.testRewireEdge
		gj.removeEdge(vX.findEdge(vY));
		gj.addEdge(new DirectedSparseEdge(vX, vZ));
		assertFalse(Ullman.areIsomorph(g, gj));
		assertFalse(Ullman.areIsomorph(gj, g));
		// restore
		gj.removeEdge(vX.findEdge(vZ));
		gj.addEdge(new DirectedSparseEdge(vX, vY));
		assertTrue(Ullman.areIsomorph(g, gj));
		assertTrue(Ullman.areIsomorph(gj, g));
		// end IsomorphTestUllman.testRewireEdge

		// add Vertex
		// begin IsomorphTestUllman.testAddRemoveVertex
		Vertex vAdd = gj.addVertex(new DirectedSparseVertex());
		assertFalse(Ullman.areIsomorph(g, gj));
		assertFalse(Ullman.areIsomorph(gj, g));
		// restore
		gj.removeVertex(vAdd);
		assertTrue(Ullman.areIsomorph(g, gj));
		assertTrue(Ullman.areIsomorph(gj, g));
		// end IsomorphTestUllman.testAddRemoveVertex

		// add Vertex & Edge
		// begin IsomorphTestUllman.testAddRemoveVertexAndEdge
		gj.addVertex(vAdd);
		gj.addEdge(new DirectedSparseEdge(vZ, vAdd));
		assertFalse(Ullman.areIsomorph(g, gj));
		assertFalse(Ullman.areIsomorph(gj, g));
		// restore
		gj.removeVertex(vAdd);
		assertTrue(Ullman.areIsomorph(g, gj));
		assertTrue(Ullman.areIsomorph(gj, g));
		// end IsomorphTestUllman.testAddRemoveVertexAndEdge

		// remove Vertex
		// begin IsomorphTestUllman.testRemoveRestoreVertex
		Set<Edge> vZedges = vZ.getIncidentEdges();
		gj.removeVertex(vZ);
		assertFalse(Ullman.areIsomorph(g, gj));
		assertFalse(Ullman.areIsomorph(gj, g));
		// restore
		gj.addVertex(vZ);
		for (Edge edge : vZedges) {
			gj.addEdge(edge);
		}
		assertTrue(Ullman.areIsomorph(g, gj));
		assertTrue(Ullman.areIsomorph(gj, g));
		// end IsomorphTestUllman.testRemoveRestoreVertex

		// test special case with self-loop
		// begin SpecialSubgraphTest.testWithSelfLoop
		Graph sg = new DirectedSparseGraph();
		Vertex s1 = sg.addVertex(new DirectedSparseVertex());
		Vertex s2 = sg.addVertex(new DirectedSparseVertex());
		Vertex s3 = sg.addVertex(new DirectedSparseVertex());
		Vertex s4 = sg.addVertex(new DirectedSparseVertex());
		Vertex s5 = sg.addVertex(new DirectedSparseVertex());

		sg.addEdge(new DirectedSparseEdge(s1, s2));
		sg.addEdge(new DirectedSparseEdge(s3, s3));
		sg.addEdge(new DirectedSparseEdge(s3, s4));
		sg.addEdge(new DirectedSparseEdge(s3, s5));

		Graph sg2 = new DirectedSparseGraph();
		Vertex ss1 = sg2.addVertex(new DirectedSparseVertex());
		Vertex ss2 = sg2.addVertex(new DirectedSparseVertex());
		Vertex ss3 = sg2.addVertex(new DirectedSparseVertex());
		Vertex ss4 = sg2.addVertex(new DirectedSparseVertex());
		Vertex ss5 = sg2.addVertex(new DirectedSparseVertex());

		sg2.addEdge(new DirectedSparseEdge(ss1, ss3));
		sg2.addEdge(new DirectedSparseEdge(ss3, ss2));
		sg2.addEdge(new DirectedSparseEdge(ss3, ss4));
		sg2.addEdge(new DirectedSparseEdge(ss3, ss5));

		assertFalse(Ullman.areIsomorph(sg, sg2));
		assertFalse(Ullman.areIsomorph(sg2, sg));
		// end SpecialSubgraphTest.testWithSelfLoop

	}

	@Test
	public void simpleSubgraphIsomorphism() {

		// one edge
		// begin testChainSubgraphIsomorphism (not the same number of vertices, but i think it works nevertheless)
		Vertex v1 = g.addVertex(new DirectedSparseVertex());
		Vertex v2 = g.addVertex(new DirectedSparseVertex());
		g.addEdge(new DirectedSparseEdge(v1, v2));

		// single Edge
		Vertex vX = gj.addVertex(new DirectedSparseVertex());
		Vertex vY = gj.addVertex(new DirectedSparseVertex());
		gj.addEdge(new DirectedSparseEdge(vX, vY));

		List subgraphs = Ullman.subgraphIsomorphism(gj, g);

		assertEquals(1, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(g, gj);

		assertEquals(1, subgraphs.size());
		// end testChainSubgraphIsomorphism

		// add another vertex
		// begin testInputMoreVertices
		Vertex v3 = g.addVertex(new DirectedSparseVertex());

		subgraphs = Ullman.subgraphIsomorphism(gj, g);

		assertEquals(1, subgraphs.size());
		// end testInputMoreVertices

		// begin testModelMoreVertices
		try {
			subgraphs = Ullman.subgraphIsomorphism(g, gj);
			fail("Expected AssertionError - assertions enabled?");
		} catch (AssertionError e) {
			assertEquals(e.getMessage(),
					"model graph must have less or equal nr of vertices as input graph");

		}
		// end testModelMoreVertices

		// remove vertex and add self-loop
		g.removeVertex(v3);
		// begin testInputSelfEdgeSubgraphIsomorphism
		g.addEdge(new DirectedSparseEdge(v1, v1));

		subgraphs = Ullman.subgraphIsomorphism(gj, g);

		assertEquals(1, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(g, gj);

		assertEquals(0, subgraphs.size());
		// end testInputSelfEdgeSubgraphIsomorphism

	}

	@Test
	public void simpleSubgraphIsomorphism2() {

		// circle
		Vertex v1 = g.addVertex(new DirectedSparseVertex());
		Vertex v2 = g.addVertex(new DirectedSparseVertex());
		Vertex v3 = g.addVertex(new DirectedSparseVertex());
		Vertex v4 = g.addVertex(new DirectedSparseVertex());
		g.addEdge(new DirectedSparseEdge(v1, v2));
		g.addEdge(new DirectedSparseEdge(v2, v3));
		g.addEdge(new DirectedSparseEdge(v3, v4));
		g.addEdge(new DirectedSparseEdge(v4, v1));

		// 2-Chain
		Vertex vX = gj.addVertex(new DirectedSparseVertex());
		Vertex vY = gj.addVertex(new DirectedSparseVertex());
		Vertex vZ = gj.addVertex(new DirectedSparseVertex());
		gj.addEdge(new DirectedSparseEdge(vX, vY));
		gj.addEdge(new DirectedSparseEdge(vY, vZ));

		List subgraphs = Ullman.subgraphIsomorphism(gj, g);

		//begin SimpleSubgraphTest.testInputCircleModelChainSubgraphIsomorphism
		assertEquals(4, subgraphs.size());
		// end SimpleSubgraphTest.testInputCircleModelChainSubgraphIsomorphism

		// begin SimpleSubgraphTest.testModelMoreVertices
		try {
			subgraphs = Ullman.subgraphIsomorphism(g, gj);
			fail("Expected AssertionError - assertions enabled?");
		} catch (AssertionError e) {
			assertEquals(e.getMessage(),
					"model graph must have less or equal nr of vertices as input graph");

		}
		// end SimpleSubgraphTest.testModelMoreVertices
		
		// System.out.println(subgraphs.size());
		// System.out.println(subgraphs);
		// assertEquals(0, subgraphs.size());

		// remove edge
		// begin SimpleSubgraphTest.testInputLongerChainSubgraphIsomorphism
		g.removeEdge(v4.findEdge(v1));

		subgraphs = Ullman.subgraphIsomorphism(gj, g);

		assertEquals(2, subgraphs.size());
		// end SimpleSubgraphTest.testInputLongerChainSubgraphIsomorphism
		// begin SimpleSubgraphTest.testModelLongerChainNoSubgraphIsomorphism
		try {
			subgraphs = Ullman.subgraphIsomorphism(g, gj);
			fail("Expected AssertionError - assertions enabled?");
		} catch (AssertionError e) {
			assertEquals(e.getMessage(),
					"model graph must have less or equal nr of vertices as input graph");

		}
		// end SimpleSubgraphTest.testModelLongerChainNoSubgraphIsomorphism
		
		// System.out.println(subgraphs.size());
		// System.out.println(subgraphs);
		// assertEquals(0, subgraphs.size());

		// self-loop
		// begin SimpleSubgraphTest.testSelfEdgeSubgraphIsomorphism
		g.addEdge(new DirectedSparseEdge(v4, v4));

		subgraphs = Ullman.subgraphIsomorphism(gj, g);

		assertEquals(2, subgraphs.size());
		// end SimpleSubgraphTest.testSelfEdgeSubgraphIsomorphism
		// begin SimpleSubgraphTest.testModelLongerChainNoSubgraphIsomorphism (no difference if there is a self edge or not)
		try {
			subgraphs = Ullman.subgraphIsomorphism(g, gj);
			fail("Expected AssertionError - assertions enabled?");
		} catch (AssertionError e) {
			assertEquals(e.getMessage(),
					"model graph must have less or equal nr of vertices as input graph");

		}
		// end SimpleSubgraphTest.testModelLongerChainNoSubgraphIsomorphism

	}

	@Test
	public void subgraphIsomorphism() {
		// for empty graphs

		// TODO

		// begin SimpleSubgraphTest.testOneCircleReflexiveEdge
		// circle
		Vertex v1 = g.addVertex(new DirectedSparseVertex());
		Vertex v2 = g.addVertex(new DirectedSparseVertex());
		Vertex v3 = g.addVertex(new DirectedSparseVertex());
		Vertex v4 = g.addVertex(new DirectedSparseVertex());
		g.addEdge(new DirectedSparseEdge(v1, v2));
		g.addEdge(new DirectedSparseEdge(v2, v3));
		g.addEdge(new DirectedSparseEdge(v3, v4));
		g.addEdge(new DirectedSparseEdge(v4, v1));

		// circle with extra reflexive edge
		Vertex vA = gi.addVertex(new DirectedSparseVertex());
		Vertex vB = gi.addVertex(new DirectedSparseVertex());
		Vertex vC = gi.addVertex(new DirectedSparseVertex());
		Vertex vD = gi.addVertex(new DirectedSparseVertex());
		gi.addEdge(new DirectedSparseEdge(vA, vB));
		gi.addEdge(new DirectedSparseEdge(vB, vC));
		gi.addEdge(new DirectedSparseEdge(vC, vD));
		gi.addEdge(new DirectedSparseEdge(vD, vA));
		gi.addEdge(new DirectedSparseEdge(vA, vD));

		List subgraphs = Ullman.subgraphIsomorphism(g, gi);

		assertEquals(4, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gi, g);

		assertEquals(0, subgraphs.size());
		// end SimpleSubgraphTest.testOneCircleReflexiveEdge

		// single Vertex
		Vertex vX = gj.addVertex(new DirectedSparseVertex());

		// begin SimpleSubgraphTest.testOneCircleReflexiveEdge
		subgraphs = Ullman.subgraphIsomorphism(g, gi);

		assertEquals(4, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gi, g);

		assertEquals(0, subgraphs.size());
		// end SimpleSubgraphTest.testOneCircleReflexiveEdge

		// begin SimpleSubgraphTest.testSingleVertexVersCircle
		subgraphs = Ullman.subgraphIsomorphism(gj, g);

		assertEquals(4, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gj, gi);

		assertEquals(4, subgraphs.size());
		// end SimpleSubgraphTest.testSingleVertexVersCircle

		// begin SimpleSubgraphTest.testSingleEdgeVersCircle
		// single Edge
		Vertex vY = gj.addVertex(new DirectedSparseVertex());
		gj.addEdge(new DirectedSparseEdge(vX, vY));

		subgraphs = Ullman.subgraphIsomorphism(gj, g);

		assertEquals(4, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gj, gi);

		assertEquals(5, subgraphs.size());
		// end SimpleSubgraphTest.testSingleEdgeVersCircle

	}

	@Test
	public void subgraphIsomorphismSelfLoops() {

		// circle
		Vertex v1 = g.addVertex(new DirectedSparseVertex());
		Vertex v2 = g.addVertex(new DirectedSparseVertex());
		Vertex v3 = g.addVertex(new DirectedSparseVertex());
		Vertex v4 = g.addVertex(new DirectedSparseVertex());
		g.addEdge(new DirectedSparseEdge(v1, v2));
		g.addEdge(new DirectedSparseEdge(v2, v3));
		g.addEdge(new DirectedSparseEdge(v3, v4));
		g.addEdge(new DirectedSparseEdge(v4, v1));

		// circle with extra self-loop and reflexive edge
		Vertex vA = gi.addVertex(new DirectedSparseVertex());
		Vertex vB = gi.addVertex(new DirectedSparseVertex());
		Vertex vC = gi.addVertex(new DirectedSparseVertex());
		Vertex vD = gi.addVertex(new DirectedSparseVertex());
		gi.addEdge(new DirectedSparseEdge(vA, vB));
		gi.addEdge(new DirectedSparseEdge(vB, vC));
		gi.addEdge(new DirectedSparseEdge(vC, vD));
		gi.addEdge(new DirectedSparseEdge(vD, vA));
		gi.addEdge(new DirectedSparseEdge(vA, vD));

		gi.addEdge(new DirectedSparseEdge(vA, vA));
		gi.addEdge(new DirectedSparseEdge(vC, vC));

		// begin SimpleSubgraphTest.testCircleReflexiveEdgeAndSelfLoop
		List subgraphs = Ullman.subgraphIsomorphism(g, gi);

		assertEquals(4, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gi, g);

		assertEquals(0, subgraphs.size());
		// end SimpleSubgraphTest.testCircleReflexiveEdgeAndSelfLoop

		// single vertex
		Vertex vX = gj.addVertex(new DirectedSparseVertex());

		subgraphs = Ullman.subgraphIsomorphism(g, gi);

		assertEquals(4, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gi, g);

		assertEquals(0, subgraphs.size());
		// begin SimpleSubgraphTest.testSingleVertexVersCircle2
		subgraphs = Ullman.subgraphIsomorphism(gj, g);
		assertEquals(4, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gj, gi);
		assertEquals(4, subgraphs.size());
		// end SimpleSubgraphTest.testSingleVertexVersCircle2

		// begin SimpleSubgraphTest.testSingleSelfLoopVersCircle
		// single self-loop
		gj.addEdge(new DirectedSparseEdge(vX, vX));
		subgraphs = Ullman.subgraphIsomorphism(gj, g);
		assertEquals(0, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gj, gi);
		assertEquals(2, subgraphs.size());
		// end SimpleSubgraphTest.testSingleSelfLoopVersCircle
	}

	@Test
	public void testVertexPair() {
		Vertex v1 = new DirectedSparseVertex();
		Vertex v2 = new DirectedSparseVertex();
		Vertex v3 = new DirectedSparseVertex();

		VertexPair p1 = new VertexPair(v1, v2);
		VertexPair p2 = new VertexPair(v1, v2);
		VertexPair p3 = new VertexPair(v1, v3);

		// begin VertexPairTest.testPairEquals
		assertEquals(p1, p2);
		// reflexive
		assertTrue(p1.equals(p2));
		assertTrue(p2.equals(p1));
		// end VertexPairTest.testPairEquals

		// begin VertexPairTest.testPairsNotEqual
		assertFalse(p1.equals(p3));
		assertFalse(p3.equals(p1));
		// end VertexPairTest.testPairsNotEqual
	}
}
