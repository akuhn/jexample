/**
 * 
 */
package extension.graph.exception;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class GraphCyclicException extends Exception {
	private static final long serialVersionUID = 1L;

	public GraphCyclicException( String string ) {
		super( string );
	}
}
