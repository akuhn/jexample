package extension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.internal.runners.InitializationError;

import extension.annotations.Depends;
import extension.graph.exception.ParentExistsException;
import extension.parser.DependencyParser;

public class MyTestMethod {

	private List<Method> dependencies;

	private boolean failed = false;

	private final Method fMethod;

	private MyTestClass fTestClass;

	private List<MyTestMethod> parentNodes;

	private boolean skipped = false;

	public MyTestMethod( Method method, MyTestClass testClass ) throws SecurityException, ClassNotFoundException, NoSuchMethodException,
	        InitializationError {
		fMethod = method;
		fTestClass = testClass;
		this.dependencies = new ArrayList<Method>();
		this.parentNodes = new ArrayList<MyTestMethod>();
		this.extractDependencies();
	}

	/**
	 * @param node the <code>TestNode</code> to be added to the parents list
	 * @throws ParentExistsException this <code>Exception</code> is thrown if a <code>TestNode</code>
	 * that already is a parent of this <code>TestNode</code> should be added. => cycle detection
	 */
	public void addParent( MyTestMethod node ) throws ParentExistsException {
		assert node != null;
		assert this.parentNodes != null;

		if ( !this.parentNodes.contains( node ) && !this.equals( node ) ) {
			this.parentNodes.add( node );
		} else {
			throw new ParentExistsException();
		}
	}

	public boolean equals( Object obj ) {
		return this.getMethod().equals( ( (MyTestMethod) obj ).getMethod() );
	}

	private void extractDependencies() throws SecurityException, ClassNotFoundException, NoSuchMethodException {

		Depends annotation = this.fMethod.getAnnotation( Depends.class );
		if ( annotation != null ) {
			this.dependencies = new DependencyParser( annotation.value(), this.fTestClass ).getDependencies();
		}
	}

	List<Method> getAfters() {
		return fTestClass.getAnnotatedMethods( After.class );
	}

	List<Method> getBefores() {
		return fTestClass.getAnnotatedMethods( Before.class );
	}

	public List<Method> getDependencies() {
		return this.dependencies;
	}

	public Method getMethod() {
		return this.fMethod;
	}

	/**
	 * @return the <code>List</code> of parents
	 */
	public List<MyTestMethod> getParents() {
		return this.parentNodes;
	}

	/**
	 * @return the represented <code>Method</code>
	 */
	public MyTestMethod getTestMethod() {
		return this;
	}

	public boolean hasFailed() {
	    return this.failed;
    }

	public void invoke( Object test ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		fMethod.invoke( test );
	}
	
	public boolean isIgnored() {
		return fMethod.getAnnotation( Ignore.class ) != null;
	}
	
	public boolean isSkipped() {
	    return this.skipped ;
    }

	public boolean parentFailedOrSkipped() {
		for ( MyTestMethod parent : this.parentNodes ) {
	        if(parent.getTestMethod().hasFailed() || parent.getTestMethod().isSkipped()){
	        	return true;
	        }
        }
	    return false;
    }

	public void setFailed() {
	    this.failed  = true;
    }



	public void setSkipped() {
	    this.skipped = true;
    }
	
	
}
