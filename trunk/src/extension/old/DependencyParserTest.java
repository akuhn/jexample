/**
 * 
 */
package extension.old;


import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class DependencyParserTest {

	private DependencyParser parser,parser2;
	private DependencyParser parser3;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MyTestClass myClass = new MyTestClass(this.getClass());
		parser = new DependencyParser("annotatedMethod",myClass);
		parser2 = new DependencyParser("annotatedMethod(java.lang.String)",myClass);
		parser3 = new DependencyParser("annotatedMethod;annotatedMethod(java.lang.String)",myClass);
	}
	
	@Test
	public void testGetDependencies() throws SecurityException, ClassNotFoundException, NoSuchMethodException{
		List<Method> methods = this.parser.getDependencies();
		assertEquals(1,methods.size());
		
		methods = this.parser2.getDependencies();
		assertEquals(1,methods.size());
		
		methods = this.parser3.getDependencies();
		assertEquals(2,methods.size());
	}
	
	
	public void annotatedMethod(){
		
	}
	
	public void annotatedMethod(String string){
		
	}

}
