package islab.graphclassifier.graph.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;

/**
 * Adapted from Ullman subgraph isomorphism algorithm as described in Appendix
 * A2 of
 * 
 * B. Messmer, “ Efficient Graph Matching Algorithms for Preprocessed Model
 * Graphs,�? Ph.D. thesis, 1996.
 * 
 * CAVEAT:
 * 
 * UNLABELED: vertices & edges are assumed to have all the same label Can be
 * directed
 * 
 * @author koenv
 * 
 */
public class Ullman {

  private static Graph g;
  private static Graph gi;

  private static int n;
  private static int m;
  private static int nrtomap;

  private static boolean areIsomorph;

  private static Vertex[] v;
  private static Vertex[] w;

  private static int[][] p;
  private static Set<VertexPair> F;

  private static ArrayList<Set<VertexPair>> subgraphs;

  // private static int tracker;

  /*****************************************************************************
   * subgraph Isomorphisms (Unlabeled directed Ullman) (see def in thesis
   * Messmer)
   ****************************************************************************/

  /**
   * Finds subgraph isomorphisms of model graph in input graph
   * 
   * Mapping is based on vertices:
   * 
   * Subgraphs induced on the input graph by the vertex mapping must not be
   * completely similar to model graph to qualify as a subgraph isomorphism,
   * greater than or equal to is enough, NOT ALL edges of the induced subgraph
   * are considered -- Different from thesis implementation -- SEE UNIT TEST FOR
   * EXAMPLE
   * 
   * NO PROVISION FOR PARALLELL EDGES ...
   * 
   * model graph must be smaller or equal to input graph
   * 
   * returns a list of all possible vertex mappings of model to input graph each
   * mapping represented as a set of VertexPairs
   * 
   * if none found, returns empty list
   * 
   * UNLABELED: vertices & edges are assumed to have all the same label
   * 
   * @author koenv
   * 
   * @return List of Vertex mappings from model graph vertices to input graph
   *         vertices
   */
  public static List<Set<VertexPair>> subgraphIsomorphism(Graph model,
      Graph input) {
    g = model;
    gi = input;
    n = g.numVertices();
    m = gi.numVertices();
    assert n <= m : "model graph must have less or equal nr of vertices as input graph";
    subgraphs = new ArrayList<Set<VertexPair>>();
    nrtomap = n;
    // put vertices in array
    v = ((Set<Vertex>)g.getVertices()).toArray(new Vertex[0]);
    w = ((Set<Vertex>)gi.getVertices()).toArray(new Vertex[0]);
    // initialize P:
    // vertices can be mapped onto each other if their labels are identical
    // in this UNLABELED implementation p[i][j] all == 1
    p = new int[n][m];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        // !! Check self-edges
        // if model graph has a self-edge, input graph needs one too
        if (v[i].isSuccessorOf(v[i]) && !w[j].isSuccessorOf(w[j])) {
          p[i][j] = 0;
        } else {
          p[i][j] = 1;
        }
      }
    }
    F = new HashSet<VertexPair>();
    backtrackSGI(p, 0, F);
    // System.out.println("subgraphs: " + subgraphs);
    return subgraphs;
  }

  private static void backtrackSGI(int[][] p, int i, Set<VertexPair> F) {
    if (i > n - 1) {
      // F is subgraph isomorphism from g to gi --> output F
      // System.out.println("F: " + F);
      subgraphs.add(new HashSet<VertexPair>(F));
      return;
    }
    for (int j = 0; j < m; j++) {
      if (p[i][j] == 1) {
        VertexPair pair = new VertexPair(v[i], w[j]);
        // System.out.println("Adding pair " + pair);
        F.add(pair);
        int[][] p_prime = new int[n][m];
        for (int s = 0; s < n; s++) {
          System.arraycopy(p[s], 0, p_prime[s], 0, m);
        }
        for (int k = i + 1; k < n; k++) {
          p_prime[k][j] = 0;
        }
        if (forward_checkingSGI(p_prime, i, F)) {
          backtrackSGI(p_prime, i + 1, F);
        }
        // System.out.println("Removing pair " + pair);
        F.remove(pair);
      }
    }
  }

  private static boolean forward_checkingSGI(int[][] p, int i, Set<VertexPair> F) {
    for (int k = i + 1; k < n; k++) {
      for (int l = 0; l < m; l++) {
        if (p[k][l] == 1) {
          for (VertexPair pair : F) { // for each pair (v, w)
            // if there is an edge vk to/from v with label X, there must be an
            // edge
            // from wl to/from w with label X
            // if there is an edge wl to/from w with label Y, there must be an
            // edge
            // from vk to/from v with label Y
            // UNLABELED --> all edge labels equal
            Vertex vee = pair.v1;
            Vertex wee = pair.v2;
            if (vee.isSuccessorOf(v[k])) {
              if (!wee.isSuccessorOf(w[l])) {
                p[k][l] = 0;
                break; // terminates loop over F
              }
            }
            if (vee.isPredecessorOf(v[k])) {
              if (!wee.isPredecessorOf(w[l])) {
                p[k][l] = 0;
                break; // terminates loop over F
              }
            }
          }
        }
      }
    }
    // see if a row with all 0 exists
    for (int k = 0; k < n; k++) {
      if (sum(p[k]) == 0) {
        return false;
      }
    }
    return true;
  }

  /*****************************************************************************
   * are Isomorph
   ****************************************************************************/

  /**
   * Checks whether the model & the input graph are isomorph
   * 
   * UNLABELED: vertices & edges are assumed to have all the same label
   * ISOMORPHISM: we do not search for subgraph isomorphisms only FULL
   * ISOMORPHISM
   * 
   * NO PROVISION FOR PARALLELL EDGES ... !!
   * 
   * @author koenv
   */
  public static boolean areIsomorph(Graph model, Graph input) {
    // SPEED HACKS are not required to pass regression testing
    g = model;
    gi = input;
    n = g.numVertices();
    m = gi.numVertices();
    nrtomap = n > m ? n : m;
    // SPEED hack: for FULL isomorphism --> need equal number of vertices &
    // edges
    if ((n != m) || (g.numEdges() != gi.numEdges()))
      return false;
    areIsomorph = false;
    // put vertices in array
    v = ((Set<Vertex>)g.getVertices()).toArray(new Vertex[0]);
    w = ((Set<Vertex>)gi.getVertices()).toArray(new Vertex[0]);
    // initialize P:
    // vertices can be mapped onto each other if their labels are identical
    // in this UNLABELED implementation p[i][j] all == 1
    // BUT we look for FULL ISOMORPHISM
    // vertices can only be mapped onto each-other if their in- AND out-degree
    // is the same
    // !! BEWARE OF SELF-EDGES: if both --> OK; if neither --> OK
    p = new int[n][m];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        if (v[i].outDegree() == w[j].outDegree()
            && v[i].inDegree() == w[j].inDegree()) {
          if (v[i].isSuccessorOf(v[i]) && w[j].isSuccessorOf(w[j])) {
            p[i][j] = 1;
          } else if (!v[i].isSuccessorOf(v[i]) && !w[j].isSuccessorOf(w[j])) {
            p[i][j] = 1;
          } else {
            p[i][j] = 0;
          }
        } else {
          p[i][j] = 0;
        }
      }
    }
    F = new HashSet<VertexPair>();
    backtrack(p, 0, F);
    // System.out.println("F: " + F);
    return areIsomorph;
  }

  private static void backtrack(int[][] p, int i, Set<VertexPair> F) {
    // if already found to be isomorph: no need to check other possible mappings
    if (areIsomorph)
      return;
    if (i > n - 1) {
      // F is a (maximal) subgraph isomorphism from g to gi --> output F
      // System.out.println("F: " + F);
      // if all vertices are mapped: graphs are isomorph
      if (F.size() == nrtomap)
        areIsomorph = true;
      return;
    }
    for (int j = 0; j < m; j++) {
      if (p[i][j] == 1) {
        VertexPair pair = new VertexPair(v[i], w[j]);
        F.add(pair);
        // System.out.println("Added pair " + pair);
        // System.out.println("F: " + F);
        int[][] p_prime = new int[n][m];
        for (int s = 0; s < n; s++) {
          System.arraycopy(p[s], 0, p_prime[s], 0, m);
        }
        for (int k = i + 1; k < n; k++) {
          p_prime[k][j] = 0;
        }
        if (forward_checking(p_prime)) {
          backtrack(p_prime, i + 1, F);
        }
        F.remove(pair);
      }
    }
  }

  private static boolean forward_checking(int[][] p) {
    // Degrees have been checked during initialisation
    // just check if a row with all 0's exists in this mapping
    for (int k = 0; k < n; k++) {
      if (sum(p[k]) == 0) {
        return false;
      }
    }
    return true;
  }

  /*****************************************************************************
   * Utility classes
   ****************************************************************************/

  public static class VertexPair {
    public final Vertex v1;
    public final Vertex v2;

    public VertexPair(Vertex v1, Vertex v2) {
      this.v1 = v1;
      this.v2 = v2;
    }

    public boolean equals(Object o) {
      // return (o instanceof VertexPair) && v1.equals(((VertexPair)o).v1) &&
      // v2.equals(((VertexPair)o).v2);
      // BUG in equals method !! v.equals(v) returns false !!!!
      // just checking Object references with == operator
      return (o != null) && (this.getClass() == o.getClass())
          && v1 == ((VertexPair)o).v1 && v2 == ((VertexPair)o).v2;
    }

    public int hashCode() {
      // Using Joshua Bloch's recipe:
      int result = 17;
      result = 37 * result + v1.hashCode();
      result = 37 * result + v2.hashCode();
      return result;
    }

    public String toString() {
      // return String.format("VertexPair: %s,%s", v1, v2);
      return String.format("(%s,%s)", v1, v2);
    }
  }

  /*****************************************************************************
   * Utility functions
   ****************************************************************************/
  private static int sum(int[] a) {
    int sum = 0;
    for (int i : a) {
      sum += i;
    }
    return sum;
  }
}
