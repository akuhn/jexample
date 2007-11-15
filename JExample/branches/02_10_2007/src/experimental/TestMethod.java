package experimental;

import java.lang.reflect.Method;
import java.util.Collection;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

enum TestResult { NOT_YET_RUN, GREEN, RED, IGNORED } 

@SuppressWarnings("unchecked")
public class TestMethod {

	private Method javaMethod;
	
	private Collection<TestMethod> dependencies;
	
	private TestResult state;

	
	public boolean belongsToClass(Class underTest) {
		return javaMethod.getDeclaringClass().equals(underTest);
	}

	public void run(RunNotifier notifier) {
		if (this.hasBeenRun()) return;
		boolean allParentsGreen = true;
		for (TestMethod dependency : dependencies) {
			dependency.run(notifier);
			allParentsGreen = allParentsGreen && dependency.isGreen();
		}
		if (allParentsGreen) {
			this.reallyDoTheRunThingee(notifier);
		}
		else {
			state = TestResult.IGNORED;
			notifier.fireTestIgnored(this.createDescription());
		}
	}
	
	private void reallyDoTheRunThingee(RunNotifier notifier) {
		// run myself and tell notifier the result, ie GREEN or RED
		// state = ...
	}

	

	public Description createDescription() {
		return Description.createTestDescription(javaMethod.getDeclaringClass(), javaMethod.getName(), javaMethod.getAnnotations());
	}

	private boolean isGreen() {
		return state == TestResult.GREEN;
	}

	private boolean hasBeenRun() {
		return state != TestResult.NOT_YET_RUN;
	}
	
	
}
