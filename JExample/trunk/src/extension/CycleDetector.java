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
 * This class checks the dependencies for cycles. If there are now cycles it
 * returns the {@link List} of all {@link Method}'s that will be run for
 * <code>testClass</code>.
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
	 * is made and every passed {@link Method} is added to the {@link List} of
	 * visited {@link Method}'s. Per traversation, the edges of the dependency
	 * graph are saved and if you find an edge which you already passed, there
	 * is a cycle and an {@link InitializationError} is thrown.
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
	 * @return true, if a cycle was detected, false otherwise
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws SecurityException
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
				deps = this.parser.getDependencies( annotation.value() );
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
