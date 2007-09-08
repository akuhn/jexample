/**
 * 
 */
package extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import extension.annotations.MyTest;

/**
 * @author Lea Haensenberger Date: Sep 7, 2007
 */
public class MyTestClass {

	private final Class<?> fClass;

	public MyTestClass( Class<?> klass ) {
		this.fClass = klass;
	}

	public List<Method> getTestMethods() {
		return this.getAnnotatedMethods( MyTest.class );
	}

	public List<Method> getAnnotatedMethods( Class<? extends Annotation> annotationClass ) {
		List<Method> results = new ArrayList<Method>();
		Method[] methods = this.fClass.getDeclaredMethods();
		for ( Method eachMethod : methods ) {
			Annotation annotation = eachMethod.getAnnotation( annotationClass );
			if ( annotation != null )
				results.add( eachMethod );
		}

		return results;
	}

	public Class<?> getJavaClass() {
		return this.fClass;
	}

	public String getName() {
		return this.fClass.getName();
	}
}
