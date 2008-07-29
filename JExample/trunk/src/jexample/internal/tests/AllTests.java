package jexample.internal.tests;

import org.junit.internal.runners.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class )
@SuiteClasses({
        AddingClasses.class,
		ComposedTestRunnerTest.class,
		CycleDetectorTest.class,
		TestDependencies.class,
		DependencyScannerTest.class,
		DependencyParserTest.class,
		DependencyValidatorTest.class,
		GraphTest.class,
		StackTest.class })
public class AllTests {
    public static void main(String[] args) {
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener());
        junit.run(AllTests.class);
    }
}
