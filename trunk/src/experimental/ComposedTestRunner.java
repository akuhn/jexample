package experimental;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class ComposedTestRunner extends Runner {

	public static TestGraph graph = TestGraph.getInstance();

	private final TestClass underTest;

	public ComposedTestRunner( Class<?> underTest ) throws InitializationError {
		this.underTest = new TestClass( underTest );
		graph.addClass( this.underTest );
	}

	@Override
	public Description getDescription() {
		return graph.descriptionForClass( this.underTest );
	}

	@Override
	public void run( RunNotifier notifier ) {
		graph.runClass( this.underTest, notifier );
	}

}
