package jexample.internal.tests;

import org.junit.internal.RealSystem;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class )
@SuiteClasses({
        CanRunPrivateInnerClass.class,
        DependenciesTest.class,
        DependsParserTest.class,
        DependsScannerTest.class,
        ExampleValidateTest.class,
        ExampleGraphAddTest.class,
        ExampleGraphTest.class,
        ForExampleTest.class,
        InjectionPolicyTest.class,
        JExampleRunnerTest.class,
        ReturnValueTest.class,
		TestClassValidationTest.class,
        demo.StackTest.class,
        demo.ListTest.class})
public class AllTests {
    public static void main(String[] args) {
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(new RealSystem()));
        junit.run(AllTests.class);
    }
}
