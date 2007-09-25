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
import extension.parser.DependencyParser;

public class MyTestMethod {

	private final Method fMethod;

	private MyTestClass fTestClass;

	private List<Method> dependencies;

	private boolean failed = false;

	private boolean skipped = false;

	public MyTestMethod( Method method, MyTestClass testClass ) throws SecurityException, ClassNotFoundException, NoSuchMethodException,
	        InitializationError {
		fMethod = method;
		fTestClass = testClass;
		this.dependencies = new ArrayList<Method>();

		this.extractDependencies();
	}

	public boolean isIgnored() {
		return fMethod.getAnnotation( Ignore.class ) != null;
	}

	List<Method> getBefores() {
		return fTestClass.getAnnotatedMethods( Before.class );
	}

	List<Method> getAfters() {
		return fTestClass.getAnnotatedMethods( After.class );
	}

	public void invoke( Object test ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		fMethod.invoke( test );
	}

	public List<Method> getDependencies() {
		return this.dependencies;
	}

	public Method getMethod() {
		return this.fMethod;
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

	public void setFailed() {
	    this.failed  = true;
    }

	public boolean hasFailed() {
	    return this.failed;
    }

	public boolean isSkipped() {
	    return this.skipped ;
    }

	public void setSkipped() {
	    this.skipped = true;
    }
}
