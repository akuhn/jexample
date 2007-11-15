package experimental;

import java.util.Collection;
import java.util.HashSet;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

@SuppressWarnings("unchecked")
public class Graph {

	private Collection<Class> classesUnderTest;
	private Collection<TestMethod> methods;
	
	public Graph() {
		classesUnderTest = new HashSet();
		methods = new HashSet();
	}
	
	public void addClass(Class underTest) throws InitializationError {
		classesUnderTest.add(underTest);
		// TODO add all test methods for this class, plus all
		// dependencies of these methods. And throws init error
		// if there is odd stuff going on in the annotations.
	}

	public Description descriptionForClass(Class underTest) {
		Description description = Description.createSuiteDescription(underTest);
		for (TestMethod method : methods) {
			if (method.belongsToClass(underTest)) {
				description.addChild(method.createDescription());
			}
		}
		return description;
	}

	public void runClass(Class underTest, RunNotifier notifier) {
		for (TestMethod method : methods) {
			if (method.belongsToClass(underTest)) {
				method.run(notifier);
			}
		}
	}

}
