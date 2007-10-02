/**
 * 
 */
package experimental;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class CycleDetector {

	private Set<TestMethod> notVisited, visited, bottomNodes;

	private HashSet<TestMethod> childsProcessed;

	public CycleDetector( Set<TestMethod> testMethods ) {
		this.notVisited = new HashSet<TestMethod>();
		this.visited = new HashSet<TestMethod>();

		this.notVisited.addAll( testMethods );
		this.bottomNodes = this.getBottomNodes();
		this.childsProcessed = new HashSet<TestMethod>();
	}

	public boolean hasCycles() {
		for ( TestMethod testMethod : this.bottomNodes ) {
			if ( this.checkNode( testMethod ) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param testMethod 
	 * @return true, if a cycle was detected, false otherwise
	 */
	private boolean checkNode( TestMethod testMethod ) {
		if ( this.childsProcessed.contains( testMethod ) && !this.notVisited.isEmpty() ) {
			return true;
		} else {			
			if ( !this.visited.contains( testMethod ) ) {
				this.visited.add( testMethod );
				this.notVisited.remove( testMethod );
			}
		}
		if ( !this.notVisitedContainsNodeWithParent( testMethod ) ) {
			this.childsProcessed.add( testMethod );
		}

		for ( TestMethod dependency : testMethod.getDependencies() ) {
			if ( this.checkNode( dependency ) ) {
				return true;
			}
		}

		return false;
	}

	private boolean notVisitedContainsNodeWithParent( TestMethod testMethod ) {
		for ( TestMethod notVisitedMethod : this.notVisited ) {
			if ( notVisitedMethod.getDependencies().contains( testMethod ) ) {
				return true;
			}
		}
		return false;
	}

	private Set<TestMethod> getBottomNodes() {
    	Set<TestMethod> bottoms = new HashSet<TestMethod>();
    	for ( TestMethod method : this.notVisited ) {
            if(!this.notVisitedContainsNodeWithParent( method )){
            	bottoms.add( method );
            }
        }
        return bottoms;
    }
}
