package extension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

public class MyTestMethod {
	private final Method fMethod;

	private MyTestClass fTestClass;

	public MyTestMethod( Method method, MyTestClass testClass ) {
		fMethod = method;
		fTestClass = testClass;
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

}
