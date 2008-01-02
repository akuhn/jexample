package islab.graphclassifier.unittests.composable;

import static org.junit.Assert.assertFalse;
import islab.graphclassifier.graph.algorithms.Ullman;

import org.junit.Test;
import org.junit.runner.RunWith;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import extension.ComposedTestRunner;
import extension.annotations.Depends;

@RunWith(ComposedTestRunner.class)
public class SpecialSubgraphTest {

	public SpecialSubgraphTest(){}
	
	@Test
	@Depends("IsomorphTestUllman.testEmptyGraphIsIsomorphToItself")
	public void testWithSelfLoop(DirectedSparseGraph sg){
		sg = ( DirectedSparseGraph ) sg.copy();
		DirectedSparseGraph sg2 = ( DirectedSparseGraph ) sg.copy();
		
		Vertex s1 = sg.addVertex(new DirectedSparseVertex());
		Vertex s2 = sg.addVertex(new DirectedSparseVertex());
		Vertex s3 = sg.addVertex(new DirectedSparseVertex());
		Vertex s4 = sg.addVertex(new DirectedSparseVertex());
		Vertex s5 = sg.addVertex(new DirectedSparseVertex());

		sg.addEdge(new DirectedSparseEdge(s1, s2));
		sg.addEdge(new DirectedSparseEdge(s3, s3));
		sg.addEdge(new DirectedSparseEdge(s3, s4));
		sg.addEdge(new DirectedSparseEdge(s3, s5));

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
	}
}
