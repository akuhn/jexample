/**
 * 
 */
package extension.graph.exception;

import java.util.Arrays;
import java.util.List;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class GraphCyclicException extends Exception {
	private static final long serialVersionUID= 1L;
	private final List<Throwable> fErrors;

	public GraphCyclicException(List<Throwable> errors) {
		fErrors= errors;
	}

	public GraphCyclicException(Throwable... errors) {
		this(Arrays.asList(errors));
	}
	
	public GraphCyclicException(String string) {
		this(new Exception(string));
	}

	public List<Throwable> getCauses() {
		return fErrors;
	}
}
