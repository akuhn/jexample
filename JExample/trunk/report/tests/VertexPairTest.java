package islab.graphclassifier.unittests.composable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import islab.graphclassifier.graph.algorithms.Ullman.VertexPair;

import org.junit.Test;
import org.junit.runner.RunWith;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import extension.ComposedTestRunner;
import extension.annotations.Depends;

@RunWith(ComposedTestRunner.class)
public class VertexPairTest {
	public VertexPairTest() {}

	@Test
	public VertexPair testPairEquals() {
		Vertex v1 = new DirectedSparseVertex();
		Vertex v2 = new DirectedSparseVertex();

		VertexPair p1 = new VertexPair(v1, v2);
		VertexPair p2 = new VertexPair(v1, v2);

		assertEquals(p1, p2);
		assertTrue(p1.equals(p2));
		assertTrue(p2.equals(p1));
		
		return p1;
	}

	@Test
	@Depends("testPairEquals")
	public void testPairsNotEqual(VertexPair p1) {
		Vertex v1 = p1.v1;
		Vertex v3 = new DirectedSparseVertex();

		VertexPair p3 = new VertexPair(v1, v3);
		assertFalse(p1.equals(p3));
		assertFalse(p3.equals(p1));
	}
}
