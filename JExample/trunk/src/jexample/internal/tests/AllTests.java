package jexample.internal.tests;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class )
@SuiteClasses( {
		ComposedTestRunnerTest.class,
		CycleDetectorTest.class,
		DependencyScannerTest.class,
		DependencyParserTest.class,
		DependencyValidatorTest.class,
		GraphTest.class,
		MethodCollectorTest.class,
		StackTest.class } )
public class AllTests {
	public static Test suite() {
		return new JUnit4TestAdapter( AllTests.class );
	}
}
