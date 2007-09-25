package extension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import extension.annotations.Depends;
import extension.parser.DependencyParser;

public class MyTestMethod {
	
	
	private final Method fMethod;

	private MyTestClass fTestClass;

	private List<Method> dependencies;

	public MyTestMethod( Method method, MyTestClass testClass ) throws SecurityException, ClassNotFoundException, NoSuchMethodException {
		fMethod = method;
		fTestClass = testClass;
		
		this.extractDependencies();
	}

	private void extractDependencies() throws SecurityException, ClassNotFoundException, NoSuchMethodException {
		this.dependencies = new ArrayList<Method>();
		
	    Depends annotation = this.fMethod.getAnnotation( Depends.class );
	    if(annotation != null){
	    	this.dependencies = new DependencyParser(annotation.value(), this.fTestClass).getDependencies();
	    }
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

	public boolean equals(Object obj){
		return this.getMethod().equals( ( (MyTestMethod) obj ).getMethod() );
	}
}
