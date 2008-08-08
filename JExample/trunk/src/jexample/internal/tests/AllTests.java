package jexample.internal.tests;

import org.junit.internal.runners.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class )
@SuiteClasses({
        ExampleGraphAddTest.class,
		JExampleRunnerTest.class,
		CycleDetectorTest.class,
		DependenciesTest.class,
		DependsScannerTest.class,
		DependsParserTest.class,
		ExampleValidateTest.class,
		ExampleGraphTest.class,
		StackTest.class })
public class AllTests {
    public static void main(String[] args) {
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener());
        junit.run(AllTests.class);
    }
}
