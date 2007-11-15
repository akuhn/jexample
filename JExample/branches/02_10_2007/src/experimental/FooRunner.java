package experimental;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

@SuppressWarnings("unchecked")
public class FooRunner extends Runner {

	public static final Graph GRAPH = new Graph();
	
	private Class underTest;
	
	public FooRunner(Class underTest) throws InitializationError {
		this.underTest = underTest;
		GRAPH.addClass(underTest);
	}
	
	@Override
	public Description getDescription() {
		return GRAPH.descriptionForClass(underTest);
	}

	@Override
	public void run(RunNotifier notifier) {
		GRAPH.runClass(underTest, notifier);
	}

}
