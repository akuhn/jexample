package jexample.internal.tests;

import org.junit.internal.runners.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class )
@SuiteClasses({
        BeforeClassTest.class,
        CanRunPrivateInnerClassTest.class,
        DependenciesTest.class,
        DependsParserTest.class,
        DependsScannerTest.class,
        ExampleValidateTest.class,
        ExampleGraphAddTest.class,
        ExampleGraphTest.class,
        ForExampleTest.class,
        InjectionPolicyTest.class,
        InheritanceTest.class,   
        JExampleTest.class,
        JExampleRunnerTest.class,
        ReturnValueTest.class,
		TestClassValidationTest.class,
        jexample.demo.StackTest.class,
        jexample.demo.ListTest.class})
public class AllTests {
    public static void main(String[] args) {
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener());
        junit.run(AllTests.class);
    }
}
