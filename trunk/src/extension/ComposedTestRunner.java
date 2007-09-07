/**
 * 
 */
package extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * @author Lea Hänsenberger Date: Sep 7, 2007
 * 
 */
public class ComposedTestRunner extends Runner {
	private MyTestClass testClass;

	private final List<Method> testMethods;

	public ComposedTestRunner( Class<?> toTest ) {
		this.testClass = new MyTestClass( toTest );
		this.testMethods = this.getTestMethods();
	}

	private List<Method> getTestMethods() {
		return this.testClass.getTestMethods();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.Runner#getDescription()
	 */
	@Override
	public Description getDescription() {
		Description spec = Description.createSuiteDescription( getName(), classAnnotations() );
		List<Method> testMethods = this.testMethods;
		for ( Method method : testMethods )
			spec.addChild( methodDescription( method ) );
		return spec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.Runner#run(org.junit.runner.notification.RunNotifier)
	 */
	@Override
	public void run( RunNotifier notifier ) {
		// TODO Auto-generated method stub

	}

	protected Description methodDescription( Method method ) {
		return Description.createTestDescription( getTestClass().getJavaClass(), testName( method ), testAnnotations( method ) );
	}

	protected String testName( Method method ) {
		return method.getName();
	}

	protected Annotation[] testAnnotations( Method method ) {
		return method.getAnnotations();
	}

	protected String getName() {
		return this.testClass.getName();
	}

	protected Annotation[] classAnnotations() {
		return this.testClass.getJavaClass().getAnnotations();
	}

	public MyTestClass getTestClass() {
		return this.testClass;
	}

}
