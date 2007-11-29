/**
 * 
 */
package extension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.internal.runners.InitializationError;

import extension.annotations.Depends;
import extension.annotations.MyTest;

/**
 * The <code>CycleDetector</code> class checks the test dependencies for cycles and collects the test methods 
 * to be run.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class CycleDetector {

	private Set< Method> bottomNodes;

	private final TestClass testClass;

	private List< Link> links;

	private List< Method> visited;

	private DependencyParser parser;

	public CycleDetector( TestClass testClass ) {
		this( testClass, null );
	}

	public CycleDetector( TestClass testClass, Method testMethod ) {
		this.testClass = testClass;
		this.bottomNodes = new HashSet< Method>();
		if(testMethod != null)
			this.bottomNodes.add( testMethod );
		this.links = new ArrayList< Link>();
		this.visited = new ArrayList< Method>();

		this.parser = new DependencyParser( this.testClass );
	}

	/**
	 * The bottom nodes are searched. For all bottom nodes a depth-first search
	 * is made and every visited {@link Method} is added to the {@link List} of
	 * visited {@link Method}'s. While traversing an arch, the edges of the dependency
	 * graph are saved and if you find an edge which you already passed a cycle detection 
	 * is made from that point on. If you find an already visited edge a {@link InitializationError} 
	 * is thrown, else the List of Methods is returned.
	 * 
	 * @return the {@link List} of all {@link Method}'s to be run
	 * @throws InitializationError
	 *             thrown if no bottom nodes are found or a cycle is found
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 */
	public List< Method> checkCyclesAndGetMethods() throws InitializationError, SecurityException,
			ClassNotFoundException, NoSuchMethodException {

		if ( this.bottomNodes.isEmpty() ) {
			try {
				this.bottomNodes = this.getBottomNodes( testClass.getAnnotatedMethods( MyTest.class ) );
			} catch ( Exception e ) {
				throw new InitializationError( e );
			}
		}

		if ( this.bottomNodes.isEmpty() ) {
			throw new InitializationError( new Exception( "The dependencies are cyclic." ) );
		}

		if ( !this.hasCycles() ) {
			return this.visited;
		}

		throw new InitializationError( new Exception( "The dependencies are cyclic." ) );
	}

	private boolean hasCycles() throws SecurityException, ClassNotFoundException, NoSuchMethodException {
		for ( Method testMethod : this.bottomNodes ) {
			this.links.clear();
			if ( this.checkNode( testMethod, null ) ) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * @param testMethod
	 * @param childMethod
	 * @return
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 */
	private boolean checkNode( Method testMethod, Method childMethod ) throws SecurityException,
	ClassNotFoundException, NoSuchMethodException {

		if ( childMethod != null ) {
			Link newLink = new Link( testMethod, childMethod );

			if ( this.links.contains( newLink ) && this.isRealCycle( childMethod ) ) {
				return true;
			} else {
				this.links.add( newLink );
				if ( !this.visited.contains( testMethod ) ) {
					this.visited.add( testMethod );
				}
			}
		} else {
			if ( !this.visited.contains( testMethod ) ) {
				this.visited.add( testMethod );
			}
		}

		List< Method> deps = this.testClass.getDependenciesFor( testMethod );
		for ( Method dependency : deps ) {
			if ( this.checkNode( dependency, testMethod ) ) {
				return true;
			}
		}

		return false;
	}

	private boolean isRealCycle( Method testMethod ) {
		CycleDetector detector = new CycleDetector( this.testClass, testMethod );
		try {
			detector.checkCyclesAndGetMethods();
		} catch ( Throwable e ) {
			return true;
		}
		return false;
	}

	private Set< Method> getBottomNodes( List< Method> methods ) throws SecurityException, ClassNotFoundException,
			NoSuchMethodException {
		Set< Method> bottoms = new HashSet< Method>();
		for ( Method method : methods ) {
			if ( !this.listContainsNodeWithParent( methods, method ) ) {
				bottoms.add( method );
			}
		}
		return bottoms;
	}

	private boolean listContainsNodeWithParent( List< Method> methods, Method method ) throws SecurityException,
			ClassNotFoundException, NoSuchMethodException {
		List< Method> deps = new ArrayList< Method>();

		for ( Method methodToCheck : methods ) {
			Depends annotation = methodToCheck.getAnnotation( Depends.class );
			if ( annotation != null ) {
				deps = this.parser.getDependencies( annotation.value(), method );
				if ( deps.contains( method ) ) {
					return true;
				}
			}
		}
		return false;
	}

	private class Link {
		private final Method child;

		private final Method parent;

		Link( Method parent, Method child ) {
			this.parent = parent;
			this.child = child;
		}

		public boolean equals( Object obj ) {
			return ( this.child.equals( ( ( Link ) obj ).child ) && this.parent.equals( ( ( Link ) obj ).parent ) );
		}

		public String toString() {
			return this.child.getName() + " " + this.parent.getName();
		}
	}
}
