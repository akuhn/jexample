/**
 * 
 */
package extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The <code>CycleDetector</code> class checks the test dependencies for cycles and collects the test methods 
 * to be run.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class CycleDetector {

	private List< TestMethod> notVisited, visited, done;

	public CycleDetector( Collection<TestMethod> testMethods ) {
		this.notVisited = new ArrayList<TestMethod>(testMethods);
		this.visited = new ArrayList<TestMethod>();
		this.done = new ArrayList<TestMethod>();
	}
	
	public boolean hasCycle(){
		List<TestMethod> nodes = new ArrayList<TestMethod>();
		nodes.addAll( this.notVisited );
		for ( TestMethod method : nodes) {
			if(this.isNotVisisted(method) && !this.processNode(method)){
				return true;
			}
		}
		return false;
	}

	private boolean processNode( TestMethod method ) {
		if(this.isVisited( method )){
			return false;
		}
		if(this.isNotVisisted( method )){
			this.addToVisited(method);
		}
		
		for ( TestMethod dep : method.getDependencies() ) {
			if(!this.processNode( dep )){
				return false;
			}
		}
		this.addToDone(method);
		return true;
	}

	private boolean isNotVisisted( TestMethod method ) {
		return this.notVisited.contains( method );
	}

	private boolean isVisited( TestMethod method ) {
		return this.visited.contains( method );
	}

	private void addToVisited( TestMethod method ) {
		this.visited.add( method );
		this.notVisited.remove( method );
	}

	private void addToDone( TestMethod method ) {
		this.done.add( method );
		this.visited.remove( method );
	}
}
