package experimental;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class FooRunner extends Runner {

	public static Graph graph = Graph.getInstance();

	private final TestClass underTest;

	public FooRunner( Class<?> underTest ) throws InitializationError {
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
