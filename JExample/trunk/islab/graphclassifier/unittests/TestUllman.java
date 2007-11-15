package islab.graphclassifier.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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


@SuppressWarnings("unchecked")
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
    System.out.println("TestUllman.testAreIsomorph()");
    // reflexive
    assertTrue(Ullman.areIsomorph(g, g));
    assertTrue(Ullman.areIsomorph(gi, gi));

    // symmetric
    assertTrue(Ullman.areIsomorph(g, gi));
    assertTrue(Ullman.areIsomorph(gi, g));
    System.out.println("Passed empty graphs test");

    Vertex v1 = g.addVertex(new DirectedSparseVertex());

    assertFalse(Ullman.areIsomorph(g, gi));
    assertFalse(Ullman.areIsomorph(gi, g));

    Vertex v2 = g.addVertex(new DirectedSparseVertex());
    Vertex v3 = g.addVertex(new DirectedSparseVertex());

    g.addEdge(new DirectedSparseEdge(v1, v2));
    g.addEdge(new DirectedSparseEdge(v2, v3));
    g.addEdge(new DirectedSparseEdge(v3, v1));

    // reflexive
    assertTrue(Ullman.areIsomorph(g, g));
    System.out.println("Passed reflexivity test");

    Vertex vA = gi.addVertex(new DirectedSparseVertex());
    Vertex vB = gi.addVertex(new DirectedSparseVertex());
    Vertex vC = gi.addVertex(new DirectedSparseVertex());
    gi.addEdge(new DirectedSparseEdge(vA, vB));
    gi.addEdge(new DirectedSparseEdge(vB, vC));
    gi.addEdge(new DirectedSparseEdge(vC, vA));

    // reflexive
    assertTrue(Ullman.areIsomorph(gi, gi));
    System.out.println("Passed reflexivity test");
    // symmetric
    assertTrue(Ullman.areIsomorph(g, gi));
    assertTrue(Ullman.areIsomorph(gi, g));
    System.out.println("Passed symmetry test");

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
    System.out.println("Passed transitivity test");

    // self-edges
    g.addEdge(new DirectedSparseEdge(v1, v1));
    gi.addEdge(new DirectedSparseEdge(vC, vC));
    assertTrue(Ullman.areIsomorph(g, gi));
    assertTrue(Ullman.areIsomorph(gi, g));
    assertFalse(Ullman.areIsomorph(g, gj));
    assertFalse(Ullman.areIsomorph(gj, g));
    // add an edge between nodes --> degree() will become the same
    g.addEdge(new DirectedSparseEdge(v2, v2));
    gj.addEdge(new DirectedSparseEdge(vY, vX));
    // should still fail
    assertFalse(Ullman.areIsomorph(g, gj));
    assertFalse(Ullman.areIsomorph(gj, g));
    // restore
    g.removeEdge(v1.findEdge(v1));
    g.removeEdge(v2.findEdge(v2));
    gi.removeEdge(vC.findEdge(vC));
    gj.removeEdge(vY.findEdge(vX));
    assertTrue(Ullman.areIsomorph(g, gi));
    assertTrue(Ullman.areIsomorph(gi, gj));
    assertTrue(Ullman.areIsomorph(gj, g));
    System.out.println("Passed self-edge test");

    // add Edge
    gj.addEdge(new DirectedSparseEdge(vX, vZ));
    assertFalse(Ullman.areIsomorph(g, gj));
    assertFalse(Ullman.areIsomorph(gj, g));
    // restore
    gj.removeEdge(vX.findEdge(vZ));
    assertTrue(Ullman.areIsomorph(g, gj));
    assertTrue(Ullman.areIsomorph(gj, g));
    System.out.println("Passed add edge test");

    // remove Edge
    gj.removeEdge(vY.findEdge(vZ));
    assertFalse(Ullman.areIsomorph(g, gj));
    assertFalse(Ullman.areIsomorph(gj, g));
    // restore
    gj.addEdge(new DirectedSparseEdge(vY, vZ));
    assertTrue(Ullman.areIsomorph(g, gj));
    assertTrue(Ullman.areIsomorph(gj, g));
    System.out.println("Passed remove edge test");

    // rewire Edge
    gj.removeEdge(vX.findEdge(vY));
    gj.addEdge(new DirectedSparseEdge(vX, vZ));
    assertFalse(Ullman.areIsomorph(g, gj));
    assertFalse(Ullman.areIsomorph(gj, g));
    // restore
    gj.removeEdge(vX.findEdge(vZ));
    gj.addEdge(new DirectedSparseEdge(vX, vY));
    assertTrue(Ullman.areIsomorph(g, gj));
    assertTrue(Ullman.areIsomorph(gj, g));
    System.out.println("Passed rewire edge test");

    // add Vertex
    Vertex vAdd = gj.addVertex(new DirectedSparseVertex());
    assertFalse(Ullman.areIsomorph(g, gj));
    assertFalse(Ullman.areIsomorph(gj, g));
    // restore
    gj.removeVertex(vAdd);
    assertTrue(Ullman.areIsomorph(g, gj));
    assertTrue(Ullman.areIsomorph(gj, g));
    System.out.println("Passed add vertex test");

    // add Vertex & Edge
    gj.addVertex(vAdd);
    gj.addEdge(new DirectedSparseEdge(vZ, vAdd));
    assertFalse(Ullman.areIsomorph(g, gj));
    assertFalse(Ullman.areIsomorph(gj, g));
    // restore
    gj.removeVertex(vAdd);
    assertTrue(Ullman.areIsomorph(g, gj));
    assertTrue(Ullman.areIsomorph(gj, g));
    System.out.println("Passed add vertex & edge test");

    // remove Vertex
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
    System.out.println("Passed remove vertex test");

    // test special case with self-loop
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
    Vertex ss1 = sg.addVertex(new DirectedSparseVertex());
    Vertex ss2 = sg.addVertex(new DirectedSparseVertex());
    Vertex ss3 = sg.addVertex(new DirectedSparseVertex());
    Vertex ss4 = sg.addVertex(new DirectedSparseVertex());
    Vertex ss5 = sg.addVertex(new DirectedSparseVertex());

    sg.addEdge(new DirectedSparseEdge(ss1, ss3));
    sg.addEdge(new DirectedSparseEdge(ss3, ss2));
    sg.addEdge(new DirectedSparseEdge(ss3, ss4));
    sg.addEdge(new DirectedSparseEdge(ss3, ss5));

    assertFalse(Ullman.areIsomorph(sg, sg2));
    assertFalse(Ullman.areIsomorph(sg2, sg));
    System.out.println("Passed special case self-loop test");

//     // test with more complex graph (100 nodes)
//     SIFFile sf = new SIFFile("gen", "type", "idx");
//     Graph x = sf.load("./data/test/input/EColi_truncated_100.sif");
//     Graph y = sf.load("./data/test/input/EColi_truncated_100_reverse_sort.sif");
//     assertTrue(Ullman.areIsomorph(x, y));
//     assertTrue(Ullman.areIsomorph(y, x));
//     // remove edge
//     Iterator<DirectedSparseEdge> it = ((Set<DirectedSparseEdge>)x.getEdges())
//         .iterator();
//     DirectedSparseEdge e1 = it.next();
//     Vertex i = e1.getSource();
//     Vertex j = e1.getDest();
//     DirectedSparseEdge e2 = it.next();
//     Vertex k = e2.getSource();
//     Vertex l = e2.getDest();
//     x.removeEdge(i.findEdge(j));
//     assertFalse(Ullman.areIsomorph(x, y));
//     assertFalse(Ullman.areIsomorph(y, x));
//     x.removeEdge(k.findEdge(l));
//     // rewire edges
//     x.addEdge(new DirectedSparseEdge(i, l));
//     assertFalse(Ullman.areIsomorph(x, y));
//     assertFalse(Ullman.areIsomorph(y, x));
//     x.addEdge(new DirectedSparseEdge(k, j));
//     assertFalse(Ullman.areIsomorph(x, y));
//     assertFalse(Ullman.areIsomorph(y, x));
//     // restore edges
//     x.removeEdge(i.findEdge(l));
//     x.removeEdge(k.findEdge(j));
//     x.addEdge(new DirectedSparseEdge(i, j));
//     x.addEdge(new DirectedSparseEdge(k, l));
//     assertTrue(Ullman.areIsomorph(x, y));
//     assertTrue(Ullman.areIsomorph(y, x));
//     System.out.println("Passed Ecoli graph test");
  }

  @Test
  public void simpleSubgraphIsomorphism() {
    System.out.println("TestUllman.simpleSubgraphIsomorphism()");
    System.out.println();
    // one edge
    Vertex v1 = g.addVertex(new DirectedSparseVertex());
    Vertex v2 = g.addVertex(new DirectedSparseVertex());
    g.addEdge(new DirectedSparseEdge(v1, v2));

    // single Edge
    Vertex vX = gj.addVertex(new DirectedSparseVertex());
    Vertex vY = gj.addVertex(new DirectedSparseVertex());
    gj.addEdge(new DirectedSparseEdge(vX, vY));

    List subgraphs = Ullman.subgraphIsomorphism(gj, g);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(1, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(g, gj);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(1, subgraphs.size());

    // add another vertex
    Vertex v3 = g.addVertex(new DirectedSparseVertex());

    subgraphs = Ullman.subgraphIsomorphism(gj, g);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(1, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(g, gj);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(0, subgraphs.size());

    // remove vertex and add self-loop
    g.removeVertex(v3);
    g.addEdge(new DirectedSparseEdge(v1, v1));

    subgraphs = Ullman.subgraphIsomorphism(gj, g);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(1, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(g, gj);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(0, subgraphs.size());

  }

@Test
  public void simpleSubgraphIsomorphism2() {
    System.out.println("TestUllman.simpleSubgraphIsomorphism2()");
    System.out.println();
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
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(4, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(g, gj);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(0, subgraphs.size());

    // remove edge
    g.removeEdge(v4.findEdge(v1));

    subgraphs = Ullman.subgraphIsomorphism(gj, g);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(2, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(g, gj);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(0, subgraphs.size());

    // self-loop
    g.addEdge(new DirectedSparseEdge(v4, v4));

    subgraphs = Ullman.subgraphIsomorphism(gj, g);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(2, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(g, gj);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(0, subgraphs.size());

  }

  @Test
  public void subgraphIsomorphism() {
    // for empty graphs
    System.out.println("TestUllman.testSubgraphIsomorphism()");
    // TODO

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
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(4, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(gi, g);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(0, subgraphs.size());

    // single Vertex
    Vertex vX = gj.addVertex(new DirectedSparseVertex());

    subgraphs = Ullman.subgraphIsomorphism(g, gi);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(4, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(gi, g);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(0, subgraphs.size());

    subgraphs = Ullman.subgraphIsomorphism(gj, g);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(4, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(gj, gi);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(4, subgraphs.size());

    // single Edge
    Vertex vY = gj.addVertex(new DirectedSparseVertex());
    gj.addEdge(new DirectedSparseEdge(vX, vY));

    subgraphs = Ullman.subgraphIsomorphism(gj, g);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(4, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(gj, gi);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(5, subgraphs.size());

//     // test with ffwd
//     System.out.println("testing ffwd");
//     subgraphs = Ullman.subgraphIsomorphism(FeedForwardStructure.getReference(),
//         FeedForwardStructure.getReference());
//     System.out.println(subgraphs.size());
//     System.out.println(subgraphs);
//     assertEquals(1, subgraphs.size());
//     // // test with more complex graph
//     // SIFFile sf = new SIFFile("gen", "type", "idx");
//     // Graph x = sf.load("./data/test/input/EColi_full.sif");
//     // Graph y = FeedForwardStructure.getReference();
//     // Ullman.subgraphIsomorphism(y, x);
  }

  @Test
  public void subgraphIsomorphismSelfLoops() {
    System.out.println("TestUllman.testSubgraphIsomorphismSelfLoops()");
    // TODO
    // System.out.println("Passed empty graphs test");

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

    List subgraphs = Ullman.subgraphIsomorphism(g, gi);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(4, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(gi, g);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(0, subgraphs.size());

    // single vertex
    Vertex vX = gj.addVertex(new DirectedSparseVertex());

    subgraphs = Ullman.subgraphIsomorphism(g, gi);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(4, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(gi, g);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(0, subgraphs.size());

    subgraphs = Ullman.subgraphIsomorphism(gj, g);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(4, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(gj, gi);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(4, subgraphs.size());

    // single self-loop
    gj.addEdge(new DirectedSparseEdge(vX, vX));

    subgraphs = Ullman.subgraphIsomorphism(gj, g);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(0, subgraphs.size());
    subgraphs = Ullman.subgraphIsomorphism(gj, gi);
    System.out.println(subgraphs.size());
    System.out.println(subgraphs);
    assertEquals(2, subgraphs.size());
  }

  @Test
  public void testVertexPair() {
    Vertex v1 = new DirectedSparseVertex();
    Vertex v2 = new DirectedSparseVertex();
    Vertex v3 = new DirectedSparseVertex();

    // System.out.println(v1==v1);
    // System.out.println(v1.equals(v1));
    // assertTrue(v1.equals(v1));
    // assertEquals(v1,v1);

    VertexPair p1 = new VertexPair(v1, v2);
    VertexPair p2 = new VertexPair(v1, v2);
    VertexPair p3 = new VertexPair(v1, v3);

    assertEquals(p1, p2);
    // reflexive
    assertTrue(p1.equals(p2));
    assertTrue(p2.equals(p1));

    assertFalse(p1.equals(p3));
    assertFalse(p3.equals(p1));
  }
}
