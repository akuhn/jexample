package extension;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * The <code>ComposedTestRunner</code> class is the Runner for composed JUnit
 * Tests. It delegates everything to the Singleton {@link TestGraph}.
 * 
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 */
public class ComposedTestRunner extends Runner {

	private static TestGraph graph = TestGraph.getInstance();

	private final TestClass underTest;

	/**
	 * @param underTest the {@link Class} to be run as a test
	 * @throws InitializationError
	 */
	public ComposedTestRunner( Class<?> underTest ) throws InitializationError {
		this.underTest = new TestClass( underTest );
		graph.addClass( this.underTest );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.Runner#getDescription()
	 */
	@Override
	public Description getDescription() {
		return graph.descriptionForClass( this.underTest );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runner.Runner#run(org.junit.runner.notification.RunNotifier)
	 */
	@Override
	public void run( RunNotifier notifier ) {
		graph.runClass( this.underTest, notifier );
	}

}
