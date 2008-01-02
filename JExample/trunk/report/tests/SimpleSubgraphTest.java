package islab.graphclassifier.unittests.composable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import islab.graphclassifier.graph.algorithms.Ullman;
import islab.graphclassifier.graph.algorithms.Ullman.VertexPair;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import extension.ComposedTestRunner;
import extension.annotations.Depends;

@RunWith(ComposedTestRunner.class)
public class SimpleSubgraphTest {
	public SimpleSubgraphTest() {
	}

	@Test
	public DirectedSparseGraph testChainSubgraphIsomorphism() {
		DirectedSparseGraph gj = new DirectedSparseGraph();

		Vertex v1 = gj.addVertex(new DirectedSparseVertex());
		Vertex v2 = gj.addVertex(new DirectedSparseVertex());
		Vertex v3 = gj.addVertex(new DirectedSparseVertex());

		gj.addEdge(new DirectedSparseEdge(v1, v2));
		gj.addEdge(new DirectedSparseEdge(v2, v3));

		DirectedSparseGraph g = (DirectedSparseGraph) gj.copy();

		List<Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, g);
		assertEquals(1, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(g, gj);
		assertEquals(1, subgraphs.size());

		return g;
	}

	@Test
	@Depends("testChainSubgraphIsomorphism")
	public DirectedSparseGraph testInputMoreVertices(DirectedSparseGraph g) {
		g = (DirectedSparseGraph) g.copy();
		DirectedSparseGraph gj = (DirectedSparseGraph) g.copy();

		g.addVertex(new DirectedSparseVertex());

		List<Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, g);
		assertEquals(1, subgraphs.size());

		return g;
	}

	@Test
	public DirectedSparseGraph testCircleSubgraphIsomorphisms() {
		DirectedSparseGraph gj = new DirectedSparseGraph();

		Vertex v1 = gj.addVertex(new DirectedSparseVertex());
		Vertex v2 = gj.addVertex(new DirectedSparseVertex());
		Vertex v3 = gj.addVertex(new DirectedSparseVertex());
		Vertex v4 = gj.addVertex(new DirectedSparseVertex());

		gj.addEdge(new DirectedSparseEdge(v1, v2));
		gj.addEdge(new DirectedSparseEdge(v2, v3));
		gj.addEdge(new DirectedSparseEdge(v3, v4));
		gj.addEdge(new DirectedSparseEdge(v4, v1));

		DirectedSparseGraph g = (DirectedSparseGraph) gj.copy();

		List<Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, g);
		assertEquals(4, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(g, gj);
		assertEquals(4, subgraphs.size());

		return g;
	}

	@Depends("testInputMoreVertices(edu.uci.ics.jung.graph.impl.DirectedSparseGraph);testChainSubgraphIsomorphism")
	public void testModelMoreVertices(DirectedSparseGraph g,
			DirectedSparseGraph gj) {
		try {
			Ullman.subgraphIsomorphism(g, gj);
			fail("Expected AssertionError - assertions enabled?");
		} catch (AssertionError e) {
			assertEquals(e.getMessage(),
					"model graph must have less or equal nr of vertices as input graph");
		}
	}

	@Test
	@Depends("testChainSubgraphIsomorphism")
	public DirectedSparseGraph testInputSelfEdgeSubgraphIsomorphism(
			DirectedSparseGraph g) {
		DirectedSparseGraph gj = (DirectedSparseGraph) g.copy();
		g = (DirectedSparseGraph) g.copy();

		for (Object vertex : g.getVertices()) {
			g.addEdge(new DirectedSparseEdge((Vertex) vertex, (Vertex) vertex));
			break;
		}

		List<Set<VertexPair>> subgraph = Ullman.subgraphIsomorphism(gj, g);
		assertEquals(1, subgraph.size());

		return g;
	}

	@Test
	@Depends("testInputSelfEdgeSubgraphIsomorphism(edu.uci.ics.jung.graph.impl.DirectedSparseGraph);testChainSubgraphIsomorphism")
	public void testModelSelfEdgeNoSubgraphIsomorphism(DirectedSparseGraph g,
			DirectedSparseGraph gj) {
		List<Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(g, gj);
		assertEquals(0, subgraphs.size());
	}

	@Test
	@Depends("testChainSubgraphIsomorphism;testCircleSubgraphIsomorphisms")
	public void testInputCircleModelChainSubgraphIsomorphism(
			DirectedSparseGraph gj, DirectedSparseGraph g) {
		List<Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, g);
		assertEquals(4, subgraphs.size());
	}

	@Depends("testChainSubgraphIsomorphism;testCircleSubgraphIsomorphisms")
	public void testModelCircleInputChainNoSubgraphIsomorphism(
			DirectedSparseGraph gj, DirectedSparseGraph g) {
		try {
			Ullman.subgraphIsomorphism(g, gj);
			fail("Expected AssertionError - assertions enabled?");
		} catch (AssertionError e) {
			assertEquals(e.getMessage(),
					"model graph must have less or equal nr of vertices as input graph");
		}
	}

	@Test
	@Depends("testChainSubgraphIsomorphism;testCircleSubgraphIsomorphisms")
	public DirectedSparseGraph testInputLongerChainSubgraphIsomorphism(
			DirectedSparseGraph gj, DirectedSparseGraph g) {
		g = (DirectedSparseGraph) g.copy();

		g.removeAllEdges();
		Vertex lastVertex = null;
		for (Object vertex : g.getVertices()) {
			if (lastVertex != null) {
				g.addEdge(new DirectedSparseEdge(lastVertex, (Vertex) vertex));
			}
			lastVertex = (Vertex) vertex;
		}

		List<Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, g);
		assertEquals(2, subgraphs.size());

		return g;
	}

	@Depends("testChainSubgraphIsomorphism;testInputLongerChainSubgraphIsomorphism(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph)")
	public void testModelLongerChainNoSubgraphIsomorphism(
			DirectedSparseGraph gj, DirectedSparseGraph g) {
		try {
			Ullman.subgraphIsomorphism(g, gj);
			fail("Expected AssertionError - assertions enabled?");
		} catch (AssertionError e) {
			assertEquals(e.getMessage(),
					"model graph must have less or equal nr of vertices as input graph");
		}
	}

	@Test
	@Depends("testChainSubgraphIsomorphism;testInputLongerChainSubgraphIsomorphism(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph)")
	public void testSelfEdgeSubgraphIsomorphism(DirectedSparseGraph gj,
			DirectedSparseGraph g) {
		g = (DirectedSparseGraph) g.copy();

		for (Object vertex : g.getVertices()) {
			g.addEdge(new DirectedSparseEdge((Vertex) vertex, (Vertex) vertex));
			break;
		}

		List<Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, g);
		assertEquals(2, subgraphs.size());
	}

	@Test
	@Depends("testCircleSubgraphIsomorphisms")
	public DirectedSparseGraph testOneCircleReflexiveEdge(DirectedSparseGraph g) {
		DirectedSparseGraph gi = (DirectedSparseGraph) g.copy();

		Edge edge = null;
		for (Object e : gi.getEdges()) {
			edge = (Edge) e;
			break;
		}
		gi.addEdge(new DirectedSparseEdge(
				((DirectedSparseEdge) edge).getDest(),
				((DirectedSparseEdge) edge).getSource()));

		List<Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(g, gi);
		assertEquals(4, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gi, g);
		assertEquals(0, subgraphs.size());

		return gi;
	}

	@Test
	@Depends("testOneCircleReflexiveEdge(edu.uci.ics.jung.graph.impl.DirectedSparseGraph);"
			+ "testCircleSubgraphIsomorphisms;"
			+ "IsomorphTestUllman.testAddVertexNotIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph)")
	public void testSingleVertexVersCircle(DirectedSparseGraph gi,
			DirectedSparseGraph g, DirectedSparseGraph gj) {
		List<Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, gi);
		assertEquals(4, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gj, g);
		assertEquals(4, subgraphs.size());
	}

	@Test
	@Depends("testOneCircleReflexiveEdge(edu.uci.ics.jung.graph.impl.DirectedSparseGraph);"
			+ "testCircleSubgraphIsomorphisms;"
			+ "IsomorphTestUllman.testAddVertexNotIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph)")
	public void testSingleEdgeVersCircle(DirectedSparseGraph gi,
			DirectedSparseGraph g, DirectedSparseGraph gj) {
		gj = (DirectedSparseGraph) gj.copy();
		Vertex vert = null;
		for (Object vertex : gj.getVertices()) {
			vert = (Vertex) vertex;
		}

		Vertex v2 = gj.addVertex(new DirectedSparseVertex());
		gj.addEdge(new DirectedSparseEdge(vert, v2));

		List<Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, gi);
		assertEquals(5, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gj, g);
		assertEquals(4, subgraphs.size());
	}

	@Test
	@Depends("testOneCircleReflexiveEdge(edu.uci.ics.jung.graph.impl.DirectedSparseGraph);testCircleSubgraphIsomorphisms")
	public DirectedSparseGraph testCircleReflexiveEdgeAndSelfLoop(
			DirectedSparseGraph gi, DirectedSparseGraph g) {

		gi = (DirectedSparseGraph) gi.copy();
		int count = 1;
		for (Object vertex : gi.getVertices()) {
			gi
					.addEdge(new DirectedSparseEdge((Vertex) vertex,
							(Vertex) vertex));
			if (count == 2) {
				break;
			}
			count++;
		}

		List<Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(g, gi);
		assertEquals(4, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gi, g);
		assertEquals(0, subgraphs.size());

		return gi;
	}

	@Test
	@Depends("testCircleReflexiveEdgeAndSelfLoop(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph);"
			+ "testCircleSubgraphIsomorphisms;"
			+ "IsomorphTestUllman.testAddVertexNotIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph)")
	public void testSingleVertexVersCircle2(DirectedSparseGraph gi,
			DirectedSparseGraph g, DirectedSparseGraph gj) {
		List<Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, gi);
		assertEquals(4, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gj, g);
		assertEquals(4, subgraphs.size());
	}

	@Test
	@Depends("testCircleReflexiveEdgeAndSelfLoop(edu.uci.ics.jung.graph.impl.DirectedSparseGraph,edu.uci.ics.jung.graph.impl.DirectedSparseGraph);"
			+ "testCircleSubgraphIsomorphisms;"
			+ "IsomorphTestUllman.testAddVertexNotIsomorph(edu.uci.ics.jung.graph.impl.DirectedSparseGraph)")
	public void testSingleSelfLoopVersCircle(DirectedSparseGraph gi,
			DirectedSparseGraph g, DirectedSparseGraph gj) {
		gj = (DirectedSparseGraph) gj.copy();

		for (Object vertex : gj.getVertices()) {
			gj
					.addEdge(new DirectedSparseEdge((Vertex) vertex,
							(Vertex) vertex));
		}

		List<Set<VertexPair>> subgraphs = Ullman.subgraphIsomorphism(gj, gi);
		assertEquals(2, subgraphs.size());
		subgraphs = Ullman.subgraphIsomorphism(gj, g);
		assertEquals(0, subgraphs.size());
	}
}
