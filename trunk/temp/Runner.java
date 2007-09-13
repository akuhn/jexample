import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import extension.annotations.MyTest;

/**
 * 
 */

/**
 * @author Lea Hänsenberger (lhaensenberger at students.unibe.ch)
 */
public class Runner {

	private final Class<?> clazz;

	private List<Method> methods;

	public Runner( Class<?> clazz ) {
		this.clazz = clazz;
		this.methods = this.getTestMethods();
	}

	private List<Method> getTestMethods() {
		Method[] methods = this.clazz.getMethods();
		List<Method> testmethods = new ArrayList<Method>();
		for ( Method method : methods ) {
			MyTest annotation = method.getAnnotation( MyTest.class );
			if ( annotation != null ) {
				testmethods.add( method );
			}
		}
		return testmethods;
	}

	@SuppressWarnings( "unchecked" )
	public List<String> run() {
		List<String> result = new ArrayList<String>();
		for ( Method method : this.methods ) {
			Depends annotation = method.getAnnotation( Depends.class );
			if ( annotation != null ) {
				try {
					Object instance = this.clazz.getConstructor().newInstance();
					result = (List<String>) method.invoke( instance, this.runDependencies( annotation.value(), instance ) );

				} catch ( IllegalArgumentException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch ( IllegalAccessException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch ( InvocationTargetException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch ( SecurityException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch ( NoSuchMethodException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch ( InstantiationException e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private Object runDependencies( String value, Object instance ) throws IllegalArgumentException, SecurityException, IllegalAccessException,
	        InvocationTargetException, NoSuchMethodException {

		return this.clazz.getMethod( value ).invoke( instance );
	}
}
