/**
 * 
 */
package extension.graph.tests;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import extension.MyTestClass;
import extension.annotations.Depends;
import extension.graph.TestGraph;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class TestGraphTest {

	private TestGraph graph;

	private List<Method> methods;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.methods = new ArrayList<Method>();
		this.methods.add( this.getClass().getMethod( "methodToAdd" ) );
		this.methods.add( this.getClass().getMethod( "methodToAdd2" ) );

		this.graph = new TestGraph( this.methods, new MyTestClass( this.getClass() ) );
	}

	@Test
	public void testCreateGraph() {

	}

	public void methodToAdd() {

	}

	@Depends( "methodToAdd" )
	public void methodToAdd2() {

	}

}
