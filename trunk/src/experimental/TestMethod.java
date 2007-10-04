package experimental;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import extension.annotations.Depends;

enum TestResult {
	NOT_YET_RUN, GREEN, RED, IGNORED
}

public class TestMethod {

	private Method javaMethod;

	private Set<TestMethod> dependencies;

	private TestResult state;

	public TestMethod( Method method ) {
		this.javaMethod = method;
		this.dependencies = new HashSet<TestMethod>();
		this.state = TestResult.NOT_YET_RUN;
    }

	public List<Method> extractDependencies(TestClass testClass ) throws SecurityException, ClassNotFoundException, NoSuchMethodException {
		List<Method> deps = new ArrayList<Method>();
		Depends annotation = this.javaMethod.getAnnotation( Depends.class );
		if ( annotation != null ) {
			deps = new DependencyParser( testClass ).getDependencies(annotation.value());
		}
		return deps;
    }

	public boolean belongsToClass( TestClass testClass ) {
		return this.javaMethod.getDeclaringClass().equals( testClass.getJavaClass() );
	}

	public void run( RunNotifier notifier ) {
		if ( this.hasBeenRun() )
			return;
		boolean allParentsGreen = true;
		for ( TestMethod dependency : this.dependencies ) {
			dependency.run( notifier );
			allParentsGreen = allParentsGreen && dependency.isGreen();
		}
		if ( allParentsGreen ) {
			this.reallyDoTheRunThingee( notifier );
		} else {
			this.state = TestResult.IGNORED;
			notifier.fireTestIgnored( this.createDescription() );
		}
	}

	private void reallyDoTheRunThingee( RunNotifier notifier ) {
	// run myself and tell notifier the result, ie GREEN or RED
	// state = ...
	}

	public Description createDescription() {
		return Description.createTestDescription( this.javaMethod.getDeclaringClass(), this.javaMethod.getName(), this.javaMethod.getAnnotations() );
	}

	private boolean isGreen() {
		return state == TestResult.GREEN;
	}

	private boolean hasBeenRun() {
		return state != TestResult.NOT_YET_RUN;
	}

	public boolean equals( Object obj ) {
		return this.javaMethod.equals( ( (TestMethod) obj ).javaMethod );
	}

	public void addDependency( TestMethod testMethod ) {
	    this.dependencies.add( testMethod );
    }

	public Set<TestMethod> getDependencies() {
	    return this.dependencies;
    }

}
