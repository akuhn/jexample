import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

/**
 * 
 */

/**
 * @author Lea Hänsenberger (lhaensenberger at students.unibe.ch)
 */
public class DependsTest {

	public DependsTest() {}

	public String runBefore() {
		return new String( "Yeah, I was run before!" );
	}

	@Depends( "runBefore" )
	public void runAfter() {

	}

	@Test
	public void testDependsAnnotation() {
		Method[] methods = this.getClass().getMethods();
		String methodToRunBefore = "";
		for ( Method method : methods ) {
			Depends annotation = method.getAnnotation( Depends.class );
			if ( annotation != null ) {
				methodToRunBefore = annotation.value();
				break;
			}
		}
		Object aString = "";
		try {
			aString = this.getClass().getMethod( methodToRunBefore ).invoke( this );
		} catch ( SecurityException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( NoSuchMethodException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( IllegalArgumentException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( IllegalAccessException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( InvocationTargetException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals( "Yeah, I was run before!", (String) aString );
	}
}
