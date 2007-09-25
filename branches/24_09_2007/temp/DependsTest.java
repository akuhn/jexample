import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import extension.annotations.MyTest;

/**
 * 
 */

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class DependsTest {

	public DependsTest() {}

	public String runBefore() {
		return new String( "Yeah, I was run before!" );
	}

	@Depends( "runBefore" )
	public void runAfter() {

	}

	@Depends( "runBefore" )
	@MyTest
	public List<String> runAfterWithParameter( String test ) {
		List<String> strings = new ArrayList<String>();
		strings.add( test );
		strings.add( new String( "And I was run afterwards!" ) );

		return strings;
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

	@Test
	public void testDependsWithParameters() {
		Runner runner = new Runner( this.getClass() );
		List<String> res = runner.run();
		assertEquals( 2, res.size() );
		assertEquals( "Yeah, I was run before!", res.get( 0 ) );
		assertEquals( "And I was run afterwards!", res.get( 1 ) );
	}
}
